aws dynamodb create-table \
  --region eu-west-2 \
  --table-name service-finder-local-postcode-location-mapping\
  --attribute-definitions AttributeName=postcode,AttributeType=S \
  --key-schema AttributeName=postcode,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
  --endpoint-url http://localhost:8000
