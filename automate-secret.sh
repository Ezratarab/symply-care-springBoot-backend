#!/bin/bash

# Encode values
DATABASE_URL=$(echo -n "jdbc:mysql://mysql-service.default.svc.cluster.local:3306/symply_care?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false" | base64)
DATABASE_USERNAME=$(echo -n "root" | base64)
DATABASE_PASSWORD=$(echo -n "ezra5385" | base64)
MAIL_HOST=$(echo -n "smtp.gmail.com" | base64)
MAIL_PORT=$(echo -n "587" | base64)
MAIL_USERNAME=$(echo -n "symplycare@gmail.com" | base64)
MAIL_PASSWORD=$(echo -n "kujs bvho t6he azjo" | base64)
RABBITMQ_HOST=$(echo -n "localhost" | base64)
RABBITMQ_PORT=$(echo -n "5672" | base64)
RABBITMQ_USERNAME=$(echo -n "guest" | base64)
RABBITMQ_PASSWORD=$(echo -n "symplycare" | base64)
RABBITMQ_QUEUE_NAME=$(echo -n "sympleCareMain" | base64)
RABBITMQ_EXCHANGE_NAME=$(echo -n "sympleCareMain_exhange" | base64)
RABBITMQ_ROUTING_KEY=$(echo -n "route_symplyCare" | base64)

# Print encoded values
echo "DATABASE_URL: $DATABASE_URL"
echo "DATABASE_USERNAME: $DATABASE_USERNAME"
echo "DATABASE_PASSWORD: $DATABASE_PASSWORD"
echo "MAIL_HOST: $MAIL_HOST"
echo "MAIL_PORT: $MAIL_PORT"
echo "MAIL_USERNAME: $MAIL_USERNAME"
echo "MAIL_PASSWORD: $MAIL_PASSWORD"
echo "RABBITMQ_HOST: $RABBITMQ_HOST"
echo "RABBITMQ_PORT: $RABBITMQ_PORT"
echo "RABBITMQ_USERNAME: $RABBITMQ_USERNAME"
echo "RABBITMQ_PASSWORD: $RABBITMQ_PASSWORD"
echo "RABBITMQ_QUEUE_NAME: $RABBITMQ_QUEUE_NAME"
echo "RABBITMQ_EXCHANGE_NAME: $RABBITMQ_EXCHANGE_NAME"
echo "RABBITMQ_ROUTING_KEY: $RABBITMQ_ROUTING_KEY"

# Create the YAML file
cat <<EOF > secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: default
type: Opaque
data:
  DATABASE_URL: $DATABASE_URL
  DATABASE_USERNAME: $DATABASE_USERNAME
  DATABASE_PASSWORD: $DATABASE_PASSWORD
  MAIL_HOST: $MAIL_HOST
  MAIL_PORT: $MAIL_PORT
  MAIL_USERNAME: $MAIL_USERNAME
  MAIL_PASSWORD: $MAIL_PASSWORD
  RABBITMQ_HOST: $RABBITMQ_HOST
  RABBITMQ_PORT: $RABBITMQ_PORT
  RABBITMQ_USERNAME: $RABBITMQ_USERNAME
  RABBITMQ_PASSWORD: $RABBITMQ_PASSWORD
  RABBITMQ_QUEUE_NAME: $RABBITMQ_QUEUE_NAME
  RABBITMQ_EXCHANGE_NAME: $RABBITMQ_EXCHANGE_NAME
  RABBITMQ_ROUTING_KEY: $RABBITMQ_ROUTING_KEY
EOF
