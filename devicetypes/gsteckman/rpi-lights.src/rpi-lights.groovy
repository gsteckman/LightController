/**
*  RPi Lights
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
	definition (name: "RPi Lights", namespace: "gsteckman", author: "Greg Steckman") {
		capability "Refresh"
		capability "Switch"
	}

	tiles(scale: 2) {
		standardTile("master", "device.switch", width: 1, height: 1, canChangeIcon: true, inactiveLabel: false) {
			state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor: "#00a0dc"
		}

		childDeviceTiles("children")
		standardTile("refreshTile", "command.refresh", width: 3, height: 3, decoration: "ring") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main("master")
		details(["master", "children", "refreshTile"])
	}
}

def installed(){
	log.debug "RPi Lights Installed"
    createChildDevices();
    refresh();
}

def updated() {
	log.debug "RPi Lights Updated"
    if (!childDevices) {
		createChildDevices()
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)
	def bodyText = msg.body
	if(bodyText != null && (bodyText.length() > 0)){
		def bodyMap = parseJson(bodyText)

		def result;
		def childDevice = childDevices.find{it.deviceNetworkId == "${bodyMap.name}"}

		if(childDevice){
			if(bodyMap.name == "overhead"){
				childDevice.sendEvent(name: "switch", value: bodyMap.state)
				childDevice.sendEvent(name: "level", value: bodMap.level)
			}else if(bodyMap.name == "hardscape"){
				childDevice.sendEvent(name: "switch", value: bodyMap.state)
				childDevice.sendEvent(name: "level", value: bodMap.level)
			}else if (bodyMap.name == "granite"){
				//childDevice.sendEvent(name: "switch", value: bodyMap.state)
			}else if(bodyMap.name == "audio"){
			}
		}

		if(childDevices.any{it.currentValue("switch") == "on"}){
			result = createEvent(name: "switch", value: "on")
		}else{
			result = createEvent(name: "switch", value: "off")
		}
	}

	return result
}

def refresh() {
	log.debug "Executing 'refresh'"
	childDevices.each{
    	it.refresh()
    }
}

def on() {
	log.debug "Executing 'on'"
	childDevices.each{
		it.on()
	}
}

def off() {
	log.debug "Executing 'off'"
	childDevices.each{
		it.off()
	}
}


def put(path, bodyMap){
	log.debug "PUT"
	def address = convertHexToIP(getDataValue("ip"))+":"+convertHexToInt(getDataValue("port"))
	def result = new physicalgraph.device.HubAction(
    	method: "PUT",
        path: path,
        HOST: address,
        headers: [
        	HOST: address
        ],
        body: bodyMap
 	)
    log.debug result
    return result;
}

def get(path){
	def address = convertHexToIP(getDataValue("ip"))+":"+convertHexToInt(getDataValue("port"))
	def result = new physicalgraph.device.HubAction(
    	method: "GET",
        path: path,
        HOST: address,
        headers: [
        	HOST: address
        ]
 	)
    return result;
}

private void createChildDevices() {
	log.debug "createChildDevices"
	state.oldLabel = device.label

    def device=addChildDevice("RPi Dimmable Light", "overhead", null,
                   [completedSetup: true, label: "Overhead",
                    isComponent: true, componentName: "overhead", componentLabel: "Overhead"])
		device.setUrl("/light/overhead");

		device=addChildDevice("RPi Dimmable Light", "hardscape", null,
                   [completedSetup: true, label: "Hardscape",
                    isComponent: true, componentName: "hardscape", componentLabel: "Hardscape"])
		device.setUrl("/light/hardscape");
}


private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
