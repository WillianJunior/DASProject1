clear
job_type=$1
repetitions=$2
delay=$3
wait_time=$4

sleep $wait_time
for i in $(seq 1 $repetitions)
do
	echo "iteration no $i"
	val=`expr $repetitions - $i`
	output_file="tests/test$job_type=$val-$i"
	rm $output_file
	username="stress$job_type=$repetitions-$i"
	echo -n "[" >> $output_file
	java ElatedClient $job_type $delay $username >> $output_file
	echo -n "]" >> $output_file
	echo "waiting..."
	sleep 5
done
