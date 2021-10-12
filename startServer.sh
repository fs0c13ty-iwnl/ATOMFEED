make
kill -9 `lsof -t -i:4567`
clear
echo "****************** INITIATING AGGREGATION SERVER *******************"
echo ""
java AggregationServer
