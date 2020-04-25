from flask import Flask, render_template

app = Flask(__name__)

@app.route('/', methods=['GET'])
def hello():

    with open("data/output.txt", "r") as file:
        incorrect_answers = file.readlines()

    return render_template('home.html',something=incorrect_answers[-1])

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0',port = 5050,threaded=True)