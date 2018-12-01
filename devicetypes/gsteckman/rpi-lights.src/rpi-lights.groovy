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
    	attribute "overheadSwitch", "string"
        attribute "overheadLevel", "number"
        
        attribute "hardscapeSwitch", "string"
        attribute "hardscapeLevel", "number"
        
        attribute "graniteSwitch", "string"
        attribute "graniteLevel", "number"
        attribute "graniteHue", "number"
        attribute "graniteSat", "number"

		capability "Refresh"
		capability "Switch"
        capability "Color Control"
        
        command "overheadOn"
        command "overheadOff"
        command "setOverheadLevel", ["number"]
        
        command "hardscapeOn"
        command "hardscapeOff"
        command "setHardscapeLevel", ["number"]

        command "graniteOn"
        command "graniteOff"
        command "setGraniteLevel", ["number"]
	}

	tiles(scale: 2) {
        standardTile ("switch", "device.switch", width: 4, height: 2, decoration: "flat") {
				state "on", label:'Main ${name}', action:"off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				state "off", label:'Main ${name}', action:"on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
				state "turningOn", label:'Main ${name}', action:"off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				state "turningOff", label:'Main ${name}', action:"on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
			}
        
        valueTile("overheadLabel", "device.overheadSwitch", width: 2, height: 2, decoration: "flat"){
        	state "overhead", label:'Overhead:'
        }
        
        standardTile ("overheadSwitch", "device.overheadSwitch", width: 2, height: 2, decoration: "flat") {
				state "on", label:'${name}', action:"overheadOff", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				state "off", label:'${name}', action:"overheadOn", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
				state "turningOn", label:'${name}', action:"overheadOff", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				state "turningOff", label:'${name}', action:"overheadOn", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
			}
        controlTile ("overheadLevel", "device.overheadLevel", "slider", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "overheadLevel", action:"setOverheadLevel"
		}
        
        valueTile("hardscapeLabel", "device.hardscapeSwitch", width: 2, height: 2, decoration: "flat"){
        	state "hardscape", label:'Hardscape:'
        }
        
        standardTile ("hardscapeSwitch", "device.hardscapeSwitch", width: 2, height: 2, decoration: "flat") {
				state "on", label:'${name}', action:"hardscapeOff", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				state "off", label:'${name}', action:"hardscapeOn", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
				state "turningOn", label:'${name}', action:"hardscapeOff", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				state "turningOff", label:'${name}', action:"hardscapeOn", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
			}
        controlTile ("hardscapeLevel", "device.hardscapeLevel", "slider", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "hardscapeLevel", action:"setHardscapeLevel"
		}

		standardTile("refreshTile", "command.refresh", width: 2, height: 2, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        valueTile("graniteLabel", "device.graniteSwitch", width: 2, height: 2, decoration: "flat"){
        	state "granite", label:'Granite:'
        }
        
        multiAttributeTile(name:"graniteSwitch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.graniteSwitch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'Granite ${name}', action:"graniteOff", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'Granite ${name}', action:"graniteOn", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'Granite ${name}', action:"graniteOff", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'Granite ${name}', action:"graniteOn", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.graniteLevel", key: "SLIDER_CONTROL") {
                attributeState "level", action:"setGraniteLevel"
            }
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"color control.setColor"
            }
        }

		main("switch")
		details(["switch", "refreshTile",
        "overheadLabel", "overheadSwitch", "overheadLevel",
        "hardscapeLabel", "hardscapeSwitch", "hardscapeLevel", 
        "graniteSwitch",
        ])
	}
}

def installed(){
	log.debug "RPi Lights Installed"
    //createChildDevices();
    refresh();
}

def updated() {
	log.debug "RPi Lights Updated"
    if (!childDevices) {
		//createChildDevices()
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)
	def bodyText = msg.body
	if(bodyText != null && (bodyText.length() > 0)){
    	log.debug bodyText
		def bodyMap = parseJson(bodyText)

			if(bodyMap.name == "overhead"){
            	sendEvent(name: "overheadSwitch", value: bodyMap.state)
				sendEvent(name: "overheadLevel", value: bodyMap.level)
            }else if(bodyMap.name == "hardscape"){
                sendEvent(name: "hardscapeSwitch", value: bodyMap.state)
				sendEvent(name: "hardscapeLevel", value: bodyMap.level)
			}else if (bodyMap.name == "granite"){
            	def newColor = [red: (int)((bodyMap.redLevel*255)/100), green: (int)((bodyMap.greenLevel*255)/100), blue: (int)((bodyMap.blueLevel*255)/100)]
				sendEvent(name: "color", value: colorUtil.rgbToHex(newColor.red, newColor.green, newColor.blue))
                def hsv = computeHSVfromRGB(bodyMap.redLevel/100, bodyMap.greenLevel/100, bodyMap.blueLevel/100)
                sendEvent(name: "graniteSwitch", value: bodyMap.state)
                sendEvent(name: "graniteLevel", value: hsv.val*100)
                sendEvent(name: "hue", value: hsv.hue*100)
                sendEvent(name: "saturation", value: hsv.sat*100)
			}else if(bodyMap.name == "audio"){
			}
        
        	def anyOn=false;
            anyOn = (device.currentValue("overheadSwitch") == "on") || (device.currentValue("hardscapeSwitch") == "on") || (device.currentValue("graniteSwitch") == "on")
            if(anyOn){
            	sendEvent(name: "switch", value: "on");
            }else{
                sendEvent(name: "switch", value: "off");
            }
	}
}

def refresh() {
	log.debug "Executing 'refresh'"
    List actions=[]    
	actions << get("/light/overhead");
    actions << get("/light/hardscape");
    actions << get("/light/granite");
    return actions
}

def on() {
	log.debug "Executing 'on'"
    
    List actions=[]    
	actions.add(put("/light/overhead", [name: "overhead", state: "on"]));
    actions.add(put("/light/hardscape", [name: "hardscape", state: "on"]));
    actions.add(put("/light/granite", [name: "granite", state: "on"]));

    return actions;
}

def off() {
	log.debug "Executing 'off'"
    
    List actions=[]
	actions.add(put("/light/overhead", [name: "overhead", state: "off"]));
    actions.add(put("/light/hardscape", [name: "hardscape", state: "off"]));
    actions.add(put("/light/granite", [name: "granite", state: "off"]));
    return actions;
}

def overheadOn(){
	log.debug "Executing 'overheadOn'"
	put("/light/overhead", [name: "overhead", state: "on"]);
}

def overheadOff(){
	log.debug "Executing 'overheadOff'"
	put("/light/overhead", [name: "overhead", state: "off"]);
}

def setOverheadLevel(level){
	log.debug "Executing 'setOverheadLevel'"
	return put("/light/overhead", [name: "overhead", level: level]);
}

def hardscapeOn(){
	log.debug "Executing 'hardscapeOn'"
	put("/light/hardscape", [name: "hardscape", state: "on"]);
}

def hardscapeOff(){
	log.debug "Executing 'hardscapeOff'"
	put("/light/hardscape", [name: "hardscape", state: "off"]);
}

def setHardscapeLevel(level){
	log.debug "Executing 'setHardscapeLevel'"
	return put("/light/hardscape", [name: "hardscape", level: level]);
}

def graniteOn(){
	log.debug "Executing 'graniteOn'"
	put("/light/granite", [name: "granite", state: "on"]);
}

def graniteOff(){
	log.debug "Executing 'graniteOff'"
	put("/light/granite", [name: "granite", state: "off"]);
}

def setColor(value) {
	log.debug "Executing 'setColor' value=${value}"
    Map rgb=computeRGBfromHSV(value.hue, value.saturation/100, device.currentValue("graniteLevel")?.toInteger()/100)
    return put("/light/granite", [name: "granite", redLevel: rgb.red, greenLevel: rgb.green, blueLevel: rgb.blue]) 
}

def setGraniteLevel(value){
	Map color = computeRGBfromHSV(device.currentValue("hue"), device.currentValue("saturation")?.toInteger()/100.0, value/100.0)
    putGraniteColor(color)
}

def putGraniteColor(value){
	return put("/light/granite", [name: "granite", redLevel: value.red, greenLevel: value.green, blueLevel: value.blue])
}

/*
* 0 <= h <= 360
* 0 <= s <= 1
* 0 <= v <= 1
*/
def Map computeRGBfromHSV(h, s, v){
	log.debug "[h, s, v] = [${h}, ${s}, ${v}]"
    def c = v*s
    def hp = h/60
    def x=c*(1-Math.abs(hp.doubleValue() % 2 - 1))
    
    log.debug "c=${c}, hp=${hp}, x=${x}"

    def color=[]
    
    if(hp <= 1 || hp >= 0){
        color=[c, x, 0]
    }else if(hp <= 2){
        color=[x, c, 0]
    }else if(hp <= 3){
    	color=[0, c, x]
    }else if(hp <= 4){
    	color=[0, x, c]
    }else if(hp <= 5){
    	color=[x, 0, c]
    }else if(hp <= 6){
    	color=[c, 0, x]
    }else{
    	color=[0, 0, 0]
    }
    
    log.debug "color=${color}"
    
    def m=v-c
    
    log.debug "m=${m}"
    
    color[0] = color[0] + m
    color[1] = color[1] + m
    color[2] = color[2] + m

    log.debug "color=${color}"
    
    return [red: (color[0]*100).toInteger(), green: (color[1]*100).toInteger(), blue: (color[2]*100).toInteger()]
}

/*
* r, g, b must be in the range [0, 1]
*/
def Map computeHSVfromRGB(r, g, b){
	def max=Math.max(r, Math.max(g, b));
    def min=Math.min(r, Math.min(g, b));
    
    def h
    
    if(max == min){
    	h=0
    }else if(max == r){
    	h = 60*((g-b)/(max-min))
    }else if(max == g){
    	h=60*(2+(b-r)/(max-min))
    }else if(max == b){
    	h=60*(4+(r-g)/(max-min))
    }
    
    if(h<0){
    	h += 360
    }
    
    def s
    if(max == 0){
    	s=0
    }else{
    	s = (max-min)/max
    }

    return [hue: h, sat: s, val: max]
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
		log.debug "calling setUrl1"
		device.setUrl("/light/overhead");

		device=addChildDevice("RPi Dimmable Light", "hardscape", null,
                   [completedSetup: true, label: "Hardscape",
                    isComponent: true, componentName: "hardscape", componentLabel: "Hardscape"])
        log.debug "calling setUrl2"
		device.setUrl("/light/hardscape");
}


private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
