This server requires Python 3.6.3 or more. You can get it from here: https://www.python.org/downloads/

To setup your python environment, run this command having this folder as the current folder in your terminal:

pip install -r requirements.txt

Once this is done, you can run the server by using the command "python main.py" or by clicking on the "main.py" file. The server will create and setup a new company with a new user for that company: it will give you the username and the password of that user for testing purposes.

Once the server is up and running, you can now run "Robotdatasim.exe" one or more times to emulate new robots that will send their data to the python server. The python server will send the data to the database.

Once you are done, you can press Ctrl+C one time on the terminal to stop the server. The server will then clean the database from the testing datas it created. 