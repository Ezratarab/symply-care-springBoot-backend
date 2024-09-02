import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
from tensorflow.python import keras
from wordcloud import WordCloud
import nltk
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import re
from apyori import apriori
import json
from nltk.stem.porter import PorterStemmer
import keras_tuner as kt

TF_ENABLE_ONEDNN_OPTS = 0

dataset_train = pd.read_csv("symptom-disease-train-dataset.csv")
dataset_test = pd.read_csv("symptom-disease-test-dataset.csv")
print(dataset_train)
print("-------------------------")
print(dataset_test)
df = pd.concat([dataset_train, dataset_test], axis=0, ignore_index=True)  # מחבר

nltk.download('stopwords')


def clean_text(text):
    text = re.sub('[^a-zA-Z]', ' ', text)  # מסיר תווים שאינם אלפביתיים מהטקסט ומחליף אותם ברווח.
    text = text.lower()  # ממירה את הטקסט לאותיות קטנות.
    text = text.split()  # מפצל את הטקסט לרשימה של מילים.
    ps = PorterStemmer()  # פונקציה להסרת סיומות מהמילים למשל מהולך כעת באנגלית להולך רגיל
    all_stopwords = stopwords.words(
        'english')  # מסיר את המילים שעוצרות את המשפט באנגלית כי הן לא חשובות כמו: הוא היא הם אני
    all_stopwords.remove('not')
    text = [ps.stem(word) for word in text if word not in set(all_stopwords)]
    text = ' '.join(text)
    return text


df['cleaned_text'] = df['text'].apply(clean_text)
corpus = df['cleaned_text']

all_text = " ".join(corpus)
wordcloud = WordCloud(width=800, height=400, background_color='white').generate(all_text)

plt.figure(figsize=(10, 5))
plt.imshow(wordcloud, interpolation='bilinear')
plt.axis('off')
plt.show()

print(len(df["cleaned_text"]))
print(len(df["label"]))

X_train, X_test, y_train, y_test = train_test_split(df["cleaned_text"], df["label"], test_size=0.2, random_state=0)
max_words = 3000

tfidf_vectorizer = TfidfVectorizer(max_features=max_words)  # for TF-IDF vectorization (words to vector)

tfidf_train = tfidf_vectorizer.fit_transform(X_train).toarray()


tfidf_test = tfidf_vectorizer.transform(X_test).toarray() #פה רק Transform כיוון שהוא למד מהשורה עליונה כבר
#צריך לעשות Transform גם לtest כי המרנו את הTrain כך אז צריך שהtest יהיה באותו המרה

y_train_unique = np.unique(y_train)
y_test_unique = np.unique(y_test)

all_numbers = np.concatenate((y_train_unique, y_test_unique))

unique_numbers = np.unique(all_numbers)
unique_numbers_sorted = np.sort(unique_numbers)

print("Unique numbers present in y_train and y_test:", unique_numbers_sorted)
print("Size of the unique numbers list:", len(unique_numbers_sorted))

num_classes = len(unique_numbers_sorted)
print(len(df["text"]))

ann_model = tf.keras.Sequential([
    tf.keras.Input((max_words,)),
    tf.keras.layers.Dense(128, activation='relu'),
    tf.keras.layers.Dropout(0.5),

    tf.keras.layers.Dense(72, activation='relu'),
    tf.keras.layers.Dropout(0.5),
    tf.keras.layers.Dense(num_classes, activation='softmax')
])

optimizer = tf.keras.optimizers.Adam(learning_rate=0.000001)

ann_model.compile(optimizer='adam',
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])

train = tf.data.Dataset.from_tensor_slices((tfidf_train, y_train))
train = train.repeat().shuffle(1000).batch(32)
#הוא לוקח 1000 נתונים ושם במחסנית ואז מתוך ה1000 הוא לוקח קבוצות של 32 ומערבב אותם ככה אינסוף פעמים כמובן ככמות האפוקים - יותר יעיל ונותן לאמן את הנתונים מחדש כל פעם
test = tf.data.Dataset.from_tensor_slices((tfidf_test, y_test)).batch(1)
print(train)


def plot_history(history):
    plt.figure(figsize=(12, 6))
    plt.subplot(1, 2, 1)
    plt.plot(history.history['accuracy'])
    plt.plot(history.history['val_accuracy'])
    plt.title('Model accuracy')
    plt.ylabel('Accuracy')
    plt.xlabel('Epoch')
    plt.legend(['Train', 'Test'], loc='upper left')

    # Plot training & validation loss values
    plt.subplot(1, 2, 2)
    plt.plot(history.history['loss'])
    plt.plot(history.history['val_loss'])
    plt.title('Model loss')
    plt.ylabel('Loss')
    plt.xlabel('Epoch')
    plt.legend(['Train', 'Test'], loc='upper left')
    plt.show()


early_stopping = keras.callbacks.EarlyStopping(monitor='val_loss', patience=20)

history = ann_model.fit(
    train,
    validation_data=test,
    steps_per_epoch=1000,
    epochs=100,
    callbacks=[early_stopping]
)

plot_history(history)

save_file = "ann_model.h5"
save_dir = os.path.join(os.getcwd(), save_file)

ann_model.save(save_dir)
print("Model saved successfully to:", save_dir)

predictions = ann_model.predict(tfidf_test)
classes = np.argmax(predictions, axis=1)

plt.figure(figsize=(8, 6))
plt.scatter(range(len(y_test)), y_test, color='green', label='Real Dots')
plt.scatter(range(len(classes)), classes, color='red', label='Predictions')
plt.title('Test Data vs Model Predictions')
plt.xlabel('Data Points')
plt.ylabel('Class Labels')
plt.legend()
plt.show()

predictions = ann_model.predict(tfidf_test)
predicted_classes = np.argmax(predictions, axis=1)

correct_indices = np.where(predicted_classes == y_test)[0]
incorrect_indices = np.where(predicted_classes != y_test)[0]

num_correct_predictions = len(correct_indices)
num_total_predictions = len(y_test)

print("Number of correct predictions:", num_correct_predictions)
print("Total number of predictions:", num_total_predictions)
print("Accuracy:", num_correct_predictions / num_total_predictions)

accuracy_score(y_test, classes)

print(ann_model.summary())

with open("mapping.json", 'r') as f:
    disease_mappings = json.load(f)

reversed_mappings = {v: k for k, v in disease_mappings.items()}

data = {
    'prediction': [],
    'disease1': [],
    'disease2': [],
    'disease3': [],
    'disease4': [],
    'disease5': [],
    'max_index': []
}
tfidf_combined = np.vstack((tfidf_train, tfidf_test))
predictions = ann_model.predict(tfidf_combined)
for pred in predictions:
    top_indices = np.argsort(pred)[-5:][::-1]
    top_diseases = [reversed_mappings[index] for index in top_indices]

    data['prediction'].append(pred)
    data['disease1'].append(top_diseases[0])
    data['disease2'].append(top_diseases[1])
    data['disease3'].append(top_diseases[2])
    data['disease4'].append(top_diseases[3])
    data['disease5'].append(top_diseases[4])
    data['max_index'].append(top_diseases[0])

aprioris = pd.DataFrame(data)

print(aprioris)

transactions = []
length = len(aprioris["prediction"])
print(length)
for i in range(0, length):
    transactions.append([str(aprioris.values[i, j]) for j in range(1, 6)])

apriori_results = apriori(transactions, min_support=0.003, min_confidence=0.2, min_lift=3, min_length=3, max_length=4)

apriori_results_list = list(apriori_results)
results_list = []
for i in range(0, len(apriori_results_list)):
    results_list.append(
        'RULE:    ' + str(apriori_results_list[i][0]) + '     SUPPORT:    ' + str(apriori_results_list[i][1]))
results_list
print(len(results_list))


def inspect(results):
    inspected_results = []
    for result in results:
        rule_match = re.match(r"RULE:\s+(.*?)\s+SUPPORT:\s+(.*)", result)
        if rule_match:
            rule_str = rule_match.group(1)
            support_str = rule_match.group(2)

            rule_elements = rule_str.split(", ")
            diseases = [element[element.find('{') + 1:element.find('}')] for element in rule_elements]
            support = float(support_str)

            if len(diseases) == 3:
                inspected_results.append((diseases[0], diseases[1], diseases[2], support))
            else:
                print("Invalid rule format:", result)
    return inspected_results


results_in_data_frame = pd.DataFrame(inspect(results_list), columns=['Disease 1', 'Disease 2', 'Disease 3', 'Support'])

sorted_results = results_in_data_frame.sort_values(by='Support', ascending=False)
print(sorted_results)

current_directory = os.path.dirname(__file__)

csv_file_path = os.path.join(current_directory, 'apriori_results.csv')

sorted_results.to_csv(csv_file_path, index=False)

print("Apriori results saved successfully to:", csv_file_path)
