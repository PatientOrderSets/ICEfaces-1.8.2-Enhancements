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
 
Ice.KeyNavigator = Class.create({
  initialize: function(componentId) {
    this.component = $(componentId);
    this.component.onkeydown = this.keydown.bindAsEventListener(this);
  },

  keydown: function(event) {
    this.srcElement = Event.element(event);
    switch(event.keyCode) {
    
        case Event.KEY_RETURN:
            this.showMenu(event);
            break;
    
        case Event.KEY_UP:
            this.goNorth(event);
            Event.stop(event);            
            break;

        case Event.KEY_DOWN:
            this.goSouth(event);
            Event.stop(event);            
            break;

        case Event.KEY_LEFT:
            this.goWest(event);
            Event.stop(event);            
            break;

        case Event.KEY_RIGHT:
            this.goEast(event);
            Event.stop(event);
            break;
    }
  },

  goNorth: function(event) {},

  goSouth: function(event) {},

  goWest: function(event) {},

  goEast: function(event) {}

});

Ice.MenuBarKeyNavigator = Class.create(Ice.KeyNavigator, {
  initialize: function($super, componentId, displayOnClick) {
    $super(componentId);
    this.displayOnClick = displayOnClick;
    this.component.onclick = this.hideAll.bindAsEventListener(this);
    document.onclick = this.hideAllDocument.bindAsEventListener(this);    
  
    if (Element.hasClassName(this.component, 'iceMnuBarVrt')) {
        this.vertical = true;
    } else {
        this.vertical = false;
    }
    this.clicked = true;
    this.configureRootItems();
  }
});

Ice.MenuBarKeyNavigator.addMethods({
  goEast: function(event) {
    this.applyFocus('e');
  },
  
  goWest: function(event) { 
    this.applyFocus('w');
  },

  goSouth: function(event) {
    this.applyFocus('s');
  },

  goNorth: function(event) { 
    this.applyFocus('n');  
  },

  focusMenuItem: function(iclass, next, direct) {
      var ci = this.srcElement.up(iclass);
      if (ci) {
          if(direct =='e') {
             var sm = $(ci.id+'_sub');
             this.focusAnchor(sm);
             return;
          }
          
          if(direct =='w') {
             var owner = $(ci.id.substring(0, ci.id.length-6));
             if (owner) {
                this.focusAnchor(owner);
             } else {
                this.focusAnchor($(ci.id.substring(0, ci.id.lastIndexOf(':'))));
             }
             return;
          }          
                                   
          var ni = null;
          if(next) {
             ni = ci.next(iclass);
          } else {
             ni = ci.previous(iclass);  
             if (!ni && direct == 'n') {
                this.focusAnchor($(ci.id.substring(0, ci.id.lastIndexOf(':'))));
                return;
             }        
          }
          this.focusAnchor(ni);
      }
  },
  
  focusSubMenuItem: function(item) {
      if (item) {
         var sm = $(item.id+'_sub');
         this.focusAnchor(sm);
      }
  },
  
  focusAnchor: function(item) {
      if (item) {
          try {
              var anch = item.down('a');
              anch.focus();
          } catch(e){}
      }
  },
  
  applyFocus: function(direct) {
      var p = this.srcElement.parentNode;
      var pm = Element.hasClassName(p, this.getPopupMenuClass());      
      var mb = Element.hasClassName(p, this.getMenuBarItemClass());
      var mi = Element.hasClassName(p, this.getMenuItemClass());

        if(mb){
            switch(direct) {
                case 's':
                if (this.vertical) 
                    this.focusMenuItem('.'+this.getMenuBarItemClass(), true);
                else
                    this.focusSubMenuItem(p);
                break;
                
                case 'e':
                if (this.vertical)
                    this.focusSubMenuItem(p);
                else
                    this.focusMenuItem('.'+this.getMenuBarItemClass(), true);                    
                break;
                
                case 'w':
                    this.focusMenuItem('.'+this.getMenuBarItemClass());
                break;
                                    
                case 'n':
                    if (this.vertical)
                        this.focusMenuItem('.'+this.getMenuBarItemClass());
                break;
            }
            
        }else if (mi) {
            this.focusMenuItem('.'+this.getMenuItemClass(), direct == 's', direct);
        }else if (pm) {
            switch(direct) {
                case 'n':
                     this.focusMenuItem('.'+this.getPopupMenuClass());
                break;
                case 's':
                     this.focusMenuItem('.'+this.getPopupMenuClass(), true);                
                break;
                case 'e':
                      this.focusSubMenuItem(p);
                break;                
                
             }                
        }
  },
  
  
  getMenuBarItemClass: function(event) {
    if (this.vertical) {
        return "iceMnuBarVrtItem";
    } else {
        return "iceMnuBarItem";
    }
  },

  getSubMenuClass: function(event) {
    if (this.vertical) {
        return "iceMnuBarVrtSubMenu";
    } else {
        return "iceMnuBarSubMenu";
    }
  },

  getSubMenuIndClass: function(event) {
    if (this.vertical) {
        return "iceMnuBarVrtSubMenuInd";
    } else {
        return "iceMnuBarSubMenuInd";
    }
  },
  
  getRootClass: function() {
    if (this.vertical) {
        return "iceMnuBarVrt";
    } else {
        return "iceMnuBar";
    }  
  },
  
  getMenuItemClass: function() {
     return "iceMnuItm";
  },
  
  getPopupMenuClass: function() {
     return "iceMnuPopVrtItem";
  },
    
  hover: function(event, element, isMouseDown) {
    if (!isMouseDown) {
        if (Ice.Menu.currentHover && Ice.Menu.currentHover == element.id) {
            //already hovered do nothing
            return;
        }
    }
    Ice.Menu.currentHover = element.id;
    if (this.clicked) {
        var submenu = $(element.id + '_sub');
        Ice.Menu.hideOrphanedMenusNotRelatedTo(element);
        if (this.vertical) {
            Ice.Menu.show(this.component,submenu,element);
        } else {
            Ice.Menu.show(element,submenu,null);
        }
    }
  },
  
  mousedown: function(event, element) {
    if (this.clicked) {
        this.clicked = false;
    } else {
        this.clicked = true;    
        this.hover(event, element, true);
    }
  },
  
  focus: function(event, element) {
    this.hover(event, element);
  },
  
  configureRootItems: function () {
    var rootLevelItems = this.component.childNodes;
    for(i=0; i < rootLevelItems.length; i++) {
        var element = rootLevelItems[i];
	    if (element.tagName == "DIV") {
	        if (Element.hasClassName(element, this.getMenuBarItemClass())) {
	            element.onmouseover = this.hover.bindAsEventListener(this, element);
		        //add focus support 
		        var anch = element.firstChild;
		        if (anch.tagName == "A") {
		            anch.onfocus = this.focus.bindAsEventListener(this, element);
		        }
		        if (this.displayOnClick) { 
		            element.onmousedown = this.mousedown.bindAsEventListener(this, element);
		            this.clicked = false;            
		        }
	        }
	    }
    }
  },
  
  hideAll:function(event) {
      element = Event.element(event); 
      var baritem = element.up('.'+ this.getMenuBarItemClass());
      var elt = event.element();
      if (elt && elt.match("a[onclick]")) {
          elt = elt.down();
      }
      if (elt) {
          elt = elt.up(".iceMnuItm a[onclick^='return false']");
      }
      if (!(baritem && this.clicked) && !elt) {
        Ice.Menu.hideAll();
        if (this.displayOnClick) {       
            this.clicked = false;
        }         
      }
      event.stopPropagation();
   },
   
   hideAllDocument:function(event) {
     if (this.displayOnClick) {       
         this.clicked = false;
     } 
     Ice.Menu.hideAll();
   },
      
   showMenu:function(event) {
     element = Event.element(event);    
     var baritem = element.up('.'+ this.getMenuBarItemClass());
     if (baritem && this.displayOnClick) {
        this.mousedown(event);
     }
   }

});
