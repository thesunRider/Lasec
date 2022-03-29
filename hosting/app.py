from flask import Flask

app = Flask(__name__)

intruder_detected = False
device_status = True

@app.route("/register_intruder")
def register_intruder():
	global intruder_detected
	print("Registered intruder")
	intruder_detected = True
	return '{"status":"ok"}'


@app.route("/get_intruder")
def get_intruder():
	global intruder_detected
	print("Called from android app")
	intruder_return = intruder_detected
	intruder_detected = False
	return '{"status":"' + str(intruder_return) + '"}'

@app.route("/device_status")
def device_status():
	global device_status
	return device_status


@app.route("/device_on")
def device_on():
	global device_status
	device_status = True
	return '{"status":"ok"}'


@app.route("/device_off")
def device_off():
	global device_status
	device_status = False
	return '{"status":"ok"}'