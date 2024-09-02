import json
import os
import re
import nltk
import numpy as np
import pandas as pd
import tensorflow.python.keras.models

from flask import Flask, request, jsonify
from flask_socketio import emit, SocketIO
from nltk import PorterStemmer
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer
import tensorflow as tf
from tensorflow.python.keras.models import load_model

TF_ENABLE_ONEDNN_OPTS=0
with open('mapping.json', 'r') as file:
    diseases = json.load(file)
saved_model_path = "ann_model.h5"
save_dir = os.path.join(os.getcwd(), saved_model_path)

ann_model = tf.keras.models.load_model(save_dir)

print(ann_model.summary)
sorted_results_csv_path = 'apriori_results.csv'
sorted_results = pd.read_csv(sorted_results_csv_path)

sorted_results = sorted_results[['Disease 1', 'Disease 2', 'Disease 3', 'Support']]
print(sorted_results)


app = Flask(__name__)

socketio = SocketIO(app, logger=False, engineio_logger=False, cors_allowed_origins="http://localhost:8080")

nltk.download('stopwords')

max_words = 3000


def clean_text(text):
    text = re.sub('[^a-zA-Z]', ' ', text)
    text = text.lower()
    text = text.split()
    ps = PorterStemmer()
    all_stopwords = stopwords.words('english')
    all_stopwords.remove('not')
    text = [ps.stem(word) for word in text if word not in set(all_stopwords)]
    text = ' '.join(text)
    return text


@app.route("/predict", methods=['POST'])
def predict():
    data = request.form.get("symptoms")
    print(data)
    tfidf_vectorizer = TfidfVectorizer(max_features=1500)
    symp = data
    inquiry = pd.DataFrame({'text': [symp]})
    inquiry['text'] = inquiry['text'].astype(str)
    inquiry['cleaned_text'] = inquiry['text'].apply(clean_text)
    tfidf_vectorizer.fit(inquiry['cleaned_text'].tolist())
    tfidf_symp = tfidf_vectorizer.transform(inquiry['cleaned_text'].tolist()).toarray()
    tfidf_length = len(tfidf_symp[0])
    new_array = np.zeros((1, max_words))
    new_array[:, :tfidf_length] = tfidf_symp
    print(new_array)
    try:
        pred = ann_model.predict(new_array)
        max_index = np.argmax(pred)
        print("The disease is :", max_index)
        search_number = max_index
        matching_disease = None
        for disease, number in diseases.items():
            if number == search_number:
                matching_disease = disease
                break

        matching_row = None
        row_number = -1
        if matching_disease is not None:
            print(f"The disease associated with the number {search_number} is: {matching_disease}")
            if "'" + matching_disease in sorted_results['Disease 1'].values:
                matching_row = sorted_results[sorted_results['Disease 1'] == "'" + matching_disease].iloc[0]
                print("Matching row from sorted first results:")
                print(matching_row)
                row_number = 1
            elif "'" + matching_disease in sorted_results['Disease 2'].values:
                matching_row = sorted_results[sorted_results['Disease 2'] == "'" + matching_disease].iloc[0]
                print("Matching row from sorted results (found in the second column):")
                print(matching_row)
                row_number = 2
            elif "'" + matching_disease in sorted_results['Disease 3'].values:
                matching_row = sorted_results[sorted_results['Disease 3'] == "'" + matching_disease + "'"].iloc[0]
                print("Matching row from sorted results (found in the second column):")
                print(matching_row)
                row_number = 3
            else:
                print("No matching row found in sorted results for the given disease.")
        else:
            print(f"No disease found associated with the number {search_number}")

    except Exception as e:
        print("Error predicting:", e)
        max_index = -1
        matching_row = None
        row_number = -1

    response_data = {
        'max_index': int(max_index),  # Convert int64 to int
        'matching_row': matching_row.to_dict() if matching_row is not None else None,
        'row_number': int(row_number),
        'mainDisease': matching_disease if matching_disease is not None else None
    }

    return jsonify(response_data)


@app.route("/", methods=['GET'])
def hello():
    return "hi"

@socketio.on('start_task')
def handle_start_task(json):
    emit('task_status', {'data': 'Starting task...'})

    def emit_status(message):
        emit('task_status', {'data': message})

    emit('task_status', {'data': 'Task completed.'})




app.run(threaded=True, debug=True, port=8500)
if __name__ == '__main__':
    socketio.run(app, debug=True)
    print(app.url_map)
