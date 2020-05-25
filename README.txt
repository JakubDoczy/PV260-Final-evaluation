# PV260-Initial-evaluation
Initial evaluation project

Tool must be executed with parameters
-d [DATASET_LOCATION_URL] -m [MANIPULATION_METHODS] -o [OUTPUT_TYPE] [OUTPUT_FILE]
All parameters must be specified but the order is not important (except the order of manipulation methods).

[MANIPULATION_METHODS]: 
  data filtration methods:
    missing_email
    missing_address
  data analysis methods:
    average_paid_price
    average_unpaid_price
    total_price_pa
    top3

[OUTPUT_TYPE]:
  xml
  plain

Examples how to run:

java -jar DaTool.jar -d file:///home/jakub/University/Projects/da_tool/src/main/resources/test_data.txt -m missing_email average_paid_price -o plain /home/jakub/University/Projects/da_tool/results.txt

java -jar DaTool.jar -o plain /home/jakub/University/Projects/da_tool/results.txt -d file:///home/jakub/University/Projects/da_tool/src/main/resources/test_data.txt -m average_paid_price missing_email top3
