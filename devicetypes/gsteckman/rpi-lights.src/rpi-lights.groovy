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
      
        standardTile("refreshTile", "command.refresh", width: 3, height: 3, decoration: "ring") {
       	 	state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
    	}

        main("master")
        details(["master", "refreshTile"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}


def refresh() {
	log.debug "Executing 'refresh'"
	// TODO: handle 'refresh' command
}

def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}