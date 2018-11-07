/**
 *  RPi Dimmable Light
 *
 *  Copyright 2018 Greg Steckman
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "RPi Dimmable Light", namespace: "gsteckman", author: "Greg Steckman") {
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
	}

	tiles {
		multiAttributeTile(name:"switchNoPower", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

//setters for parent
public void setUrl(String u){
	state.url = u;
}

// handle commands
def refresh(){
	log.debug "Executing 'refresh'"
	parent.sendHubCommand(parent.get("state.url"));
}

def on() {
	log.debug "Executing 'on'"
    doPut("on", device.latestValue("level") as Integer ?: 0)
}

def off() {
	log.debug "Executing 'off'"
    doPut("off", device.latestValue("level") as Integer ?: 0)
}

def setLevel() {
	log.debug "Executing 'setLevel'"
    doPut(device.latestValue("switch"), device.latestValue("level") as Integer ?: 0)
}

def doPut(state, level){
	def body=[name: "${device.deviceNetworkId}", state: "${state}", level: level]
	parent.sendHubCommand(parent.put(state.url, body))
}
