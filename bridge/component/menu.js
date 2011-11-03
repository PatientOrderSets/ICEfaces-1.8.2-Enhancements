
/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

Ice.Menu = Class.create();
Ice.Menu = {
    menuContext:null,
    currentMenu:null,
    currentHover:null,    
    openMenus:new Array(0),
    printOpenMenus:function() {
    	var openMenuString = '';
        for (var i = 0; i < Ice.Menu.openMenus.length; i++) {
            openMenuString = openMenuString + Ice.Menu.openMenus[i].id + ' , ';
        }
        return openMenuString;
    },
    printHoverMenuAndOpenMenus: function(hoverMenu) {
    	alert('hoverMenu=[' + hoverMenu.id + ']\n'
    		+'openMenus=[' + Ice.Menu.printOpenMenus() + ']');
    },
    printArray: function(arrayToPrint) {
    	var buffer = '';
		for (var i = 0; i < arrayToPrint.length; i++) {
			buffer = buffer + arrayToPrint[i] + ', ';
		}    	
		return buffer;
    },
    printArrayOfIds: function(arrayToPrint) {
    	var buffer = '';
		for (var i = 0; i < arrayToPrint.length; i++) {
			buffer = buffer + arrayToPrint[i].id + ', ';
		}    	
		return buffer;
    },
    hideAll: function(){
        for (var i = 0; i < Ice.Menu.openMenus.length; i++ ) {
            if (Ice.Menu.openMenus[i].iframe) Ice.Menu.openMenus[i].iframe.hide(); // ICE-2066, ICE-2912
            Ice.Menu.openMenus[i].style.display='none';
        }
        Ice.Menu.openMenus = new Array();
        Ice.Menu.currentMenu = null;
        Ice.Menu.menuContext = null;
        Ice.Menu.currentHover = null;        
    },
    getPosition: function(element,positionProperty) {
	    var position = 0;
	    while (element != null) {
		    position += element["offset" + positionProperty];
		    element = element.offsetParent;
	    }
	    return position;
    },
    show: function(supermenu,submenu,submenuDiv) { 
	    if (submenu) {
	        var menu = $(submenu);
	        //menu is already visible, don't do anything
            if (menu && menu.style.display=='') return;
            Ice.Menu.showMenuWithId(submenu);
            var supmVPO = supermenu.viewportOffset(),
                submVPO = submenu.viewportOffset(),
                viewport = document.viewport,
                supmOW = supermenu.offsetWidth,
                submOW = submenu.offsetWidth,  
                submOH = submenu.offsetHeight,  
                supmOH = supermenu.offsetHeight;
                submenuDiv = $(submenuDiv);
            if (submenuDiv) {
                var subdOH = submenuDiv.offsetHeight;
                // ICE-3196, ICE-3620
                if (supmVPO.left + supmOW + submOW < viewport.getWidth()) {
                    if(Prototype.Browser.IE)
                        Ice.clonePositionIE(submenu, supermenu, {setTop:false, setWidth:false, setHeight:false, offsetLeft:supmOW}, supmVPO);
                    else
                        submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false, offsetLeft:supmOW});
                    
                } else {
                    if(Prototype.Browser.IE)
                       Ice.clonePositionIE(submenu, supermenu, {setTop:false, setWidth:false, setHeight:false, offsetLeft:- submOW}, supmVPO);
                    else 
                       submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false, offsetLeft:- submOW});
                    
                }
                if (submenuDiv.viewportOffset().top + submOH < viewport.getHeight()) {
                    if(Prototype.Browser.IE)
                        Ice.clonePositionIE(submenu, submenuDiv, {setLeft:false, setWidth:false, setHeight:false});
                    else 
                        submenu.clonePosition(submenuDiv, {setLeft:false, setWidth:false, setHeight:false});          
                } else {
                    if(Prototype.Browser.IE)                
                      Ice.clonePositionIE(submenu, submenuDiv, {setLeft:false, setWidth:false, setHeight:false,
                         offsetTop:- submOH + subdOH});   
                    else
                        submenu.clonePosition(submenuDiv, {setLeft:false, setWidth:false, setHeight:false,
                            offsetTop:- submOH + subdOH});
                }
            } else {
                // ICE-3196, ICE-3620
                if (supmVPO.left + submOW < viewport.getWidth()) {
                    if(Prototype.Browser.IE)   
                       Ice.clonePositionIE(submenu, supermenu, {setTop:false, setWidth:false, setHeight:false}, supmVPO);
                    else              
                       submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false});
                                    
                } else {
                    if(Prototype.Browser.IE)   
                         Ice.clonePositionIE(submenu, supermenu, {setTop:false, setWidth:false, setHeight:false,
                           offsetLeft:viewport.getWidth() - supmVPO.left - submOW}, supmVPO); 
	                else
	                    submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false,
                           offsetLeft:viewport.getWidth() - supmVPO.left - submOW});
                
                }
                if (supmVPO.top + supmOH + submOH < viewport.getHeight()) {
                    if(Prototype.Browser.IE)
                        Ice.clonePositionIE(submenu, supermenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:supmOH}, supmVPO);
                    else                 
                        submenu.clonePosition(supermenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:supmOH});
                     
                } else {
                    if(Prototype.Browser.IE)   
                       Ice.clonePositionIE(submenu, supermenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:- submOH}, supmVPO);
                    else              
                       submenu.clonePosition(supermenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:- submOH});
                      
                }
            }
            if (submVPO.top < 0) { // ICE-3658
                if(Prototype.Browser.IE)
                    Ice.clonePositionIE(submenu, submenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:- submVPO.top}, submVPO);
                else
                    submenu.clonePosition(submenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:- submVPO.top});
            }
            Ice.Menu.showIframe(submenu); // ICE-2066, ICE-2912
	    }
        Ice.Menu.currentMenu = submenu;
    },
    showPopup: function(showX, showY, submenu) {
        Ice.Menu.hideAll();
        submenu=$(submenu);
	    if (submenu) {
            Ice.Menu.showMenuWithId(submenu);
            var styleLeft = showX + "px";
            submenu.style.left = styleLeft;
            
            var styleTop = showY  + "px";
            submenu.style.top = styleTop;
            Ice.Menu.showIframe(submenu); // ICE-2066, ICE-2912
        }
        Ice.Menu.currentMenu = submenu;
    },
    showIframe: function(menuDiv) { // ICE-2066, ICE-2912
        if (!Prototype.Browser.IE) return;
        if (parseFloat(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE") + 5)) >= 7) return;
        var iframe = menuDiv.iframe;
        if (!iframe) {
            var sendURI = Ice.ElementModel.Element.adaptToElement(menuDiv).findConnection().sendURI;
            var webappContext = sendURI.substring(0, sendURI.indexOf("block/send-receive-updates"));
            var iframeSrc = webappContext + "xmlhttp/blank";
            menuDiv.iframe = iframe = new Element("iframe", {src: iframeSrc, frameborder: "0", scrolling: "no"});
            iframe.setStyle({position: "absolute", opacity: 0}).hide();
            menuDiv.insert({before: iframe});
        }
        if (Prototype.Browser.IE) {
            Ice.clonePositionIE(iframe, menuDiv);
        } else {
            Element.clonePosition(iframe, menuDiv);
        }
        iframe.show();
    },
    contextMenuPopup: function(event, popupMenu, targComp) {
        var dynamic = $(popupMenu + "_dynamic");
        if(!event) {
        	event = window.event;
        }
        if(event) {
        	event.returnValue = false;
        	event.cancelBubble  = true;

            if(event.stopPropagation) {
            	event.stopPropagation();
            }
            
            var posx = 0; // Mouse position relative to
            var posy = 0; //  the document
            if (event.pageX || event.pageY) 	{
                posx = event.pageX;
                posy = event.pageY;
            }
            else if (event.clientX || event.clientY) 	{
                posx = event.clientX + document.body.scrollLeft
                    + document.documentElement.scrollLeft;
                posy = event.clientY + document.body.scrollTop
                    + document.documentElement.scrollTop;
            }
            if (dynamic) {
                    dynamic.value = posx +", "+ posy + ", "+ popupMenu + ", "+ targComp;
                try {
                    var form = Ice.util.findForm(dynamic);
                    iceSubmitPartial(form,dynamic,event);
                } catch (e) {logger.info("Form not found" + e);}
                return;
            }
            
           Ice.Menu.showIt(posx, posy, popupMenu, targComp);
        }
    },
    showIt: function(posx, posy, popupMenu, targComp) {
            Ice.Menu.showPopup(posx, posy, popupMenu.strip());
            Event.observe(document, "click", Ice.Menu.hidePopupMenu);
            Ice.Menu.setMenuContext(targComp.strip());    
    },
    
    setMenuContext: function(mnuCtx) {
        if(Ice.Menu.menuContext == null) {
            Ice.Menu.menuContext = mnuCtx;
        }
    },
    hideOrphanedMenusNotRelatedTo: function(hoverMenu) {
    	// form an array of allowable names
    	var relatedMenus = new Array();
    	var idSegments = hoverMenu.id.split(':');
		var nextRelatedMenu = '';
		for (var i=0; i<idSegments.length; i++) {
			nextRelatedMenu = nextRelatedMenu + idSegments[i];
			var concatArray = new Array(nextRelatedMenu + '_sub');
			relatedMenus = relatedMenus.concat(concatArray);
			nextRelatedMenu = nextRelatedMenu + ':';
		}
		// iterate over open menus and set display='none' for any menu
		// that is not in the array of menus related to the current menu
		var arrayLength = Ice.Menu.openMenus.length;
		var menusToHide = new Array();
		for (var j = 0; j < arrayLength; j ++) {
			var nextOpenMenu = $(Ice.Menu.openMenus[j]);
			var found = 'false';
			for (var k = 0; k < relatedMenus.length; k++) {
				if (nextOpenMenu.id == relatedMenus[k]) {
					found = 'true';
				}
			}
			if (found != 'true') {
				menusToHide[menusToHide.length] = nextOpenMenu.id;
                if(nextOpenMenu == Ice.Menu.currentMenu) {
                    Ice.Menu.currentMenu = null;
                }
            }
		}
		// iterate over the menus to hide
		Ice.Menu.hideMenusWithIdsInArray(menusToHide);
    },
    hideSubmenu: function(hoverMenu) {
    	var cur = Ice.Menu.currentMenu;
    	var hoverParentId = hoverMenu.id.substring(0,hoverMenu.id.lastIndexOf(':'));
		var curParentId = cur.id.substring(0,cur.id.lastIndexOf(':'));
		if (hoverParentId == curParentId) {
			Ice.Menu.hideMenuWithId(Ice.Menu.currentMenu);
		}
    },
    hideMenusWithIdsInArray: function(menuIdArray) {
    	if (menuIdArray) {
    		for (var i = 0; i < menuIdArray.length; i ++) {
    			Ice.Menu.hideMenuWithId(menuIdArray[i]);
    		}
    	}
    },
    hideMenuWithId: function(menu) {
        menu = $(menu);
        if (menu) {
            if (menu.iframe) menu.iframe.hide(); // ICE-2066, ICE-2912
	    	menu.style.display='none';
	    	Ice.Menu.removeFromOpenMenus(menu);
    	}
    	return;
    },
    removeFromOpenMenus: function(menu) {
    	var tempArray = new Array();
    	for (var i = 0; i < Ice.Menu.openMenus.length; i ++) {
    		if (Ice.Menu.openMenus[i].id != menu.id) {
    			tempArray = tempArray.concat(new Array(Ice.Menu.openMenus[i]));
    		}
    	}
    	Ice.Menu.openMenus = tempArray;
    },
    showMenuWithId: function(menu) {
    	if (menu) {
    		menu = $(menu);
    		menu.style.display='';
			Ice.Menu.addToOpenMenus(menu);
    	}
    },
    addToOpenMenus: function(menu) {
		if (menu) {
			menu = $(menu);
			var found = 'false';
	    	for (var i = 0; i < Ice.Menu.openMenus.length; i ++) {
	    		if (Ice.Menu.openMenus[i].id == menu.id) {
	    			found = 'true';
	    			break;
	    		}
	    	}
	    	if (found != 'true') {
	            var openMenu = new Array(menu);
	            Ice.Menu.openMenus = Ice.Menu.openMenus.concat(openMenu);
	    	}
	    	
		}
    },
    appendHoverClasses: function(menuItem) {
        var styleClasses = menuItem.className.replace(/^\s+|\s+$/g, "").split(/\s+/);
        if (styleClasses[0] == "") return;

        for (var i = 0; i < styleClasses.length; i++) {
            if (styleClasses[i] == "portlet-menu-item-selected") {
                menuItem.className += " portlet-menu-item-hover-selected";
            } else {
                menuItem.className += " " + styleClasses[i] + "-hover";
            }
        }
    },
    removeHoverClasses: function(menuItem) {
        var n = menuItem.className.replace(/^\s+|\s+$/g, "").split(/\s+/).length / 2;
        var regExp = new RegExp("( portlet-menu-item-hover-selected| \\S+-hover){" + n + "}$");
        menuItem.className = menuItem.className.replace(regExp, "");
    },
    hidePopupMenu:function() {
        Ice.Menu.hideAll();
        Event.stopObserving(document, "click", Ice.Menu.hidePopupMenu);
    },
    hideOnMouseOut: function(rootID, event) {
        if (!event) {
        	event = window.event;
        }
        if (event) {
            var element;
            element = event.relatedTarget;
            if (!element) element = event.toElement;
            if (element) {
                if (!Ice.Menu.isInMenu(element, rootID)) {
                    Ice.Menu.hideAll();
                }
            }
        }
    },
    isInMenu: function(element, rootID) {
        if (element.id == rootID) return true;
        if (element == undefined || element == document) return false;
        return Ice.Menu.isInMenu(element.parentNode, rootID);
    }
};

//modified version of Prototype's Element.clonePosition for IE
Ice.clonePositionIE = function(element, source, options, sourceVOS) {
      logger.info('Using clonePosition() optimized for IE');
        var options = Object.extend({
            setLeft:    true,
            setTop:     true,
            setWidth:   true,
            setHeight:  true,
            offsetTop:  0,
            offsetLeft: 0
        }, arguments[2] || { });
        element = $(element);
    // find page position of source
        var p = null;
        if (sourceVOS){
            p = sourceVOS;
        } else {
            p = source.viewportOffset();
        }
        


    // find coordinate system to use
        var delta = [0, 0];
        var parent = null;
    // delta [0,0] will do fine with position: fixed elements, 
        // position:absolute needs offsetParent deltas
        if (Element.getStyle(element, 'position') == 'absolute') {
            parent = element.getOffsetParent();
            
            var top = Element.getStyle(parent, 'top');
            var left = Element.getStyle(parent, 'left');
            var bdyScrollTop = document.documentElement.scrollTop;
            var bdyScrollLeft = document.documentElement.scrollLeft;            
            var repositioned = false;
            
            _viewportOffset =  parent['_viewportOffset'];
            
            if (!_viewportOffset) {
                parent['_top'] = null;
                parent['_left'] = null;
                parent['_bodyScrollTop'] = bdyScrollTop;
                parent['_bodyScrollLeft'] = bdyScrollLeft;                
                repositioned = true;
            } else {
                repositioned = !((parent['_top'] == top && parent['_left'] == left) && 
                (parent['_bodyScrollTop'] == bdyScrollTop && parent['_bodyScrollLeft'] == bdyScrollLeft)); 
            }
           
            parent['_top'] = top;
            parent['_left'] = left;
            parent['_bodyScrollTop'] = bdyScrollTop;
            parent['_bodyScrollLeft'] = bdyScrollLeft;   
            if(repositioned) {
                delta = parent.viewportOffset();
                parent['_viewportOffset'] = delta;                 
            } else {
               delta = parent['_viewportOffset'];
            }
        }

    // set position
        if (options.setLeft)   element.style.left = (p[0] - delta[0] + options.offsetLeft) + 'px';
        if (options.setTop)    element.style.top = (p[1] - delta[1] + options.offsetTop) + 'px';
        if (options.setWidth)  element.style.width = source.offsetWidth + 'px';
        if (options.setHeight) element.style.height = source.offsetHeight + 'px';
        return element;
    };

