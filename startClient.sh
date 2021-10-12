clear
echo "************************* INITIATING TESTS *************************"
echo ""
java -classpath . GETClient http://127.0.0.1:4567 ATOMClient/1/0

echo "********************** GETClient tests passed **********************"
echo ""
java -classpath . ContentServer http://127.0.0.1:4567 ATOMClient1
java -classpath . ContentServer http://127.0.0.1:4567 ATOMClient2
java -classpath . ContentServer http://127.0.0.1:4567 ATOMClient3
java -classpath . ContentServer http://127.0.0.1:4567 ATOMClient4

echo "******************** ContentServer tests passed ********************"
echo ""
