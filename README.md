# Program Instruction
## Classifying documents

To classify a document or documents in a folder, open program folder in terminal, type commmand:

	./classify.sh argument1 argument2
	
where argument1 is the path to the folder containing training data, for example: 

	20news-bydate/20news-bydate-train
	
and argument2 is the path to a document or the path to a folder containing documents, for example:

	20news-bydate/20news-bydate-test/alt.atheism/53068
	
or

	20news-bydate/20news-bydate-test/alt.atheism
	
The program will output the document name and its class.

## Testing program

To test the program, open program folder in terminal, type command:

	./test.sh argument1 argument2
	
where argument1 is the path to the folder containing training data, for example:

	20news-bydate/20news-bydate-train
	
and argument2 is the path to the folder containing testing data, for example:

	20news-bydate/20news-bydate-test
	
The program will output the accuracy of each class and an average accuracy for all the classes.

If you encounter Denied Permission error, type the command:

	chmod 777 classify.sh
	chmod 777 test.sh	
