clear
job_type=$1
repetitions=$2

for i in $(seq 1 $repetitions)
do
	echo "iteration no $i"
	val=`expr $repetitions - $i`
	output_file="tests/test$job_type=$val-$i"
	rm $output_file
	username="stress$job_type=$repetitions-$i"
	echo -n "[" >> $output_file
	java ElatedClient $job_type 0 $username >> $output_file
	echo -n "]" >> $output_file
	echo "waiting..."
	sleep 5
done
