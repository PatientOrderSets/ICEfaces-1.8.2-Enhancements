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
Ice.Resizable = Class.create({
  initialize: function(event, horizontal) {

    //resize handler
    this.source = Event.element(event);
    this.horizontal = horizontal;

    //initial pointer location
    if (this.horizontal) {
        this.pointerLocation = parseInt(Event.pointerY(event));
    } else {
        this.pointerLocation = parseInt(Event.pointerX(event));
    }

    this.eventMouseMove = this.resize.bindAsEventListener(this);
    this.eventMouseUp = this.detachEvent.bindAsEventListener(this);
    Event.observe(document, "mousemove", this.eventMouseMove);
    Event.observe(document, "mouseup", this.eventMouseUp);
    this.origionalHeight = this.source.style.height;
    this.disableTextSelection();
    this.getGhost().style.position = "absolute";
//    this.getGhost().style.backgroundColor = "green";
//    this.getGhost().style.border= "1px dashed";

    this.deadPoint = 20;
  },

  print: function(msg) {
    logger.info(msg);
  },

  getPreviousElement: function() {},

  getContainerElement: function() {},

  getNextElement: function() {},

  getGhost:function() {
    return this.source;
  },

  finalize: function (event) {
    this.source.style.position = "";
    this.source.style.left= Event.pointerX(event) + "px";
 //   this.source.style.backgroundColor = "#EFEFEF";
 //   this.source.style.border = "none";
  },

  resize: function(event) {
    this.getGhost().style.visibility="";
    if (this.deadEnd(event)) return;
 //   this.getGhost().style.backgroundColor = "green";
    if(this.horizontal) {
        this.getGhost().style.cursor="n-resize";
        var top = Event.pointerY(event) - this.getGhost().getOffsetParent().cumulativeOffset().top;
        this.getGhost().style.top = top + "px";
    } else {
        this.getGhost().style.cursor="e-resize";
        var left = Event.pointerX(event) - this.getGhost().getOffsetParent().cumulativeOffset().left;
        this.getGhost().style.left = left + "px";
    }
  },


  detachEvent: function(event) {
    //restore height
    this.source.style.height =  this.origionalHeight;
    if (this.getDifference(event) > 0 && !this.deadEnd(event)) {
        this.adjustPosition(event);
    }

    Event.stopObserving(document, "mousemove", this.eventMouseMove);
    Event.stopObserving(document, "mouseup", this.eventMouseUp);
    this.enableTextSelection();
    this.finalize(event);
  },

  adjustPosition:function(event) {
    var leftElementWidth = Element.getWidth(this.getPreviousElement());
    var rightElementWidth = Element.getWidth(this.getNextElement());
    var tableWidth = Element.getWidth(this.getContainerElement());
    var diff = this.getDifference(event);

    if (this.resizeAction == "inc") {
        this.getPreviousElement().style.width = (leftElementWidth + diff) + "px";
        this.getNextElement().style.width = (rightElementWidth - diff) + "px"

    //    this.getContainerElement().style.width = tableWidth + diff + "px";;

        this.print("Diff "+ diff);
        this.print("Td width "+ leftElementWidth + this.getPreviousElement().id);
        this.print("Table width "+ tableWidth);


    } else {
        this.getPreviousElement().style.width = (leftElementWidth - diff) + "px";
        this.getNextElement().style.width = (rightElementWidth + diff) + "px"

  //      this.getContainerElement().style.width = tableWidth - diff + "px";
    }
  },

  getDifference: function(event) {
    var x;
     if (this.horizontal) {
        x = parseInt(Event.pointerY(event));
     } else {
        x = parseInt(Event.pointerX(event));
     }
    if (this.pointerLocation > x) {
        this.resizeAction = "dec";
        return this.pointerLocation - x;
    } else {
        this.resizeAction = "inc";
        return x -this.pointerLocation;
    }
  },

  deadEnd: function(event) {
    var diff = this.getDifference(event);
    if (this.resizeAction == "dec") {
        var leftElementWidth;
        if(this.horizontal) {
            leftElementWidth = Element.getHeight(this.getPreviousElement());
        } else {
            leftElementWidth = Element.getWidth(this.getPreviousElement());
        }

        if ((leftElementWidth - diff) < this.deadPoint) {
          // this.getGhost().style.backgroundColor = "red";
           return true;
        }
    } else {
        var rightElementWidth;
        if(this.horizontal) {
            rightElementWidth = Element.getHeight(this.getNextElement());
        } else {
            rightElementWidth = Element.getWidth(this.getNextElement());
        }

        if ((rightElementWidth - diff) < this.deadPoint) {
       //    this.getGhost().style.backgroundColor = "red";
           return true;
        }
    }
    return false;
  },

  disableTextSelection:function() {
    this.getContainerElement().onselectstart = function () { return false; }
    this.source.style.unselectable = "on";
    this.source.style.MozUserSelect = "none";
    this.source.style.KhtmlUserSelect ="none";
  },

    enableTextSelection:function() {
    this.getContainerElement().onselectstart = function () { return true; }
    this.source.style.unselectable = "";
    this.source.style.MozUserSelect = "";
    this.source.style.KhtmlUserSelect = "";
  }
});

Ice.ResizableGrid = Class.create(Ice.Resizable, {
  initialize: function($super, event) {
    $super(event);    logger.info(">>>>>>>>>>>>>>>>>>> ");
    this.cntHght = (Element.getHeight(this.getContainerElement())) + "px";
    this.source.style.height = this.cntHght;
    this.getGhost().style.left= Event.pointerX(event) + "px";
    this.source.style.backgroundColor="#CCCCCC";
  }
});

Ice.ResizableGrid.addMethods({
  getDifference: function($super, event) {
    return $super(event);
  },

  getContainerElement: function() {
      return this.source.parentNode.parentNode.parentNode.parentNode;
  },

  getPreviousElement: function() {
    if (this.source.parentNode.previousSibling.tagName == "TH") {
        return this.source.parentNode.previousSibling.firstChild;
    } else {
        return this.source.parentNode.previousSibling.previousSibling.firstChild;
    }
  },

  getNextElement: function() {
    if (this.source.parentNode.nextSibling.tagName == "TH") {
        return this.source.parentNode.nextSibling.firstChild;
    } else {
        return this.source.parentNode.nextSibling.nextSibling.firstChild;
    }
  },
  
  resize: function($super, event) {
    this.source.style.height = this.cntHght;
    this.getGhost().style.height = this.cntHght;    
    $super(event);    
    this.source.style.height = this.cntHght;
    this.getGhost().style.height = this.cntHght;    
  },
  
  finalize: function ($super, event) {
     $super(event);
     this.source.style.height = "1px";
     this.source.style.backgroundColor="transparent";
     this.getGhost().style.height = "1px";
     var clientOnly = $(this.getContainerElement().id + "clientOnly");
     if (clientOnly) {
        clientOnly.value = this.getAllColumnsWidth();
        var form = Ice.util.findForm(clientOnly);
        iceSubmitPartial(form,clientOnly,event);
     }     
  },
  
  getAllColumnsWidth:function() {
    var container = this.getContainerElement();
    var children = container.firstChild.firstChild.childNodes;
    var gap = 2;
    if (Prototype.Browser.Gecko) {
        gap+=2;
    }
    var widths ="";
    for (i=0; i < children.length; i++) {
        if (i%gap==0) {
           widths += Element.getStyle(children[i].firstChild, "width") + ",";
        }
    } 
    return widths;
  }    
  

});

Ice.PanelDivider = Class.create(Ice.Resizable, {
  initialize: function($super, event, horizontal) {
    $super(event, horizontal);
    this.deadPoint = 20;
    if (this.horizontal) {
        var spliterHeight = Element.getHeight(this.source);
        var mouseTop = Event.pointerY(event);
        this.getGhost().style.top = (mouseTop - (spliterHeight )) + "px";
        this.getGhost().style.width = (Element.getWidth(this.getContainerElement())) + "px";
    } else {
        var spliterWidth = Element.getWidth(this.source);
        var borderLeft = parseInt(Element.getStyle(this.source, 'border-left-width'));
        var borderRight = parseInt(Element.getStyle(this.source, 'border-right-width'));
        if (borderLeft && borderLeft >= 1) {
            spliterWidth -= borderLeft;
        }
        if (borderRight && borderRight >= 1) {
            spliterWidth -= borderRight;
        }     
        var mouseLeft = Event.pointerX(event);
        this.getGhost().style.left = (mouseLeft - (spliterWidth )) + "px";
        this.getGhost().style.width =  spliterWidth + "px";
        this.getGhost().style.height = (Element.getHeight(this.getContainerElement())) + "px";
    }
  }
});

Ice.PanelDivider.addMethods({
  getDifference: function($super, event) {
    return $super(event);
  },

  getContainerElement: function() {
      return this.source.parentNode.parentNode;
  },


  getPreviousElement: function() {
    if (this.source.previousSibling.tagName == "DIV") {
        return this.source.previousSibling;
    } else {
        return this.source.previousSibling.previousSibling;
    }
  },

  getNextElement: function() {
    if (this.source.nextSibling.tagName == "DIV") {
        return this.source.nextSibling;
    } else {
        return this.source.nextSibling.nextSibling;
    }
  },

  getGhost: function() {
    if(!this.ghost) {
        this.ghost = this.source.cloneNode(true);
        this.ghost.id = this.source.id + ":ghost";
        this.ghost.onmousedown = null;
        this.source.parentNode.appendChild(this.ghost);
        this.ghost.style.width = Element.getWidth(this.source) + "px";
        this.getGhost().style.visibility="hidden";
    }
      this.ghost.setStyle({width:this.source.getStyle("width")});
    return this.ghost;
  },

  finalize: function (event) {
    Element.remove(this.ghost);
  },

  adjustPosition:function(event) {logger.info("<<<<<<<<<<<<<<<<<<<<< ADJUST POSTITITITITITI >>>>>>>>>>>>>>>>");
      var savedVisibility = this.getNextElement().style.visibility;
      this.getNextElement().style.visibility = "hidden";
   if (this.horizontal) {
        var leftElementHeight = (Element.getHeight(this.getPreviousElement()));
        var rightElementHeight = (Element.getHeight(this.getNextElement()));

        var tableHeight = Element.getHeight(this.getContainerElement());
        var totalHeight = (parseInt(leftElementHeight) + parseInt(rightElementHeight));
        var diff = this.getDifference(event);
        var inPercent;
        if (this.resizeAction == "inc") {
            inPercent = (leftElementHeight + diff) /tableHeight  ;
            topInPercent = Math.round(inPercent * 100);      
            bottomInPercent = 99 - topInPercent;                    
            this.getPreviousElement().style.height = (topInPercent)   + "%";
//            this.getNextElement().style.height = bottomInPercent + "%"

        } else {
            inPercent = (leftElementHeight - diff) / tableHeight ;
            topInPercent = Math.round(inPercent * 100);      
            bottomInPercent = 99 - topInPercent;                    
            this.getPreviousElement().style.height = (topInPercent) + "%";
//            this.getNextElement().style.height = bottomInPercent + "%"

        }
   } else {
        var leftElementWidth = (Element.getWidth(this.getPreviousElement()));
        var rightElementWidth = (Element.getWidth(this.getNextElement()));
        var splitterWidth = (Element.getWidth(this.source)); 
        var tableWidth = Element.getWidth(this.getContainerElement());
        var totalWidth = (parseInt(leftElementWidth) + parseInt(rightElementWidth));
        var diff = this.getDifference(event);
        if (this.resizeAction == "inc") {
            inPercent = (leftElementWidth + diff) /tableWidth  ;
            leftInPercent = Math.round(inPercent * 100);      
            rightInPercent = 100 - leftInPercent;
            this.getPreviousElement().style.width = leftInPercent + "%";
//            this.getNextElement().style.width = rightInPercent + "%"


        } else {
            inPercent = (leftElementWidth - diff) / tableWidth ; 
            leftInPercent = Math.round(inPercent * 100); 
            rightInPercent = 100 - leftInPercent;
            this.getPreviousElement().style.width = leftInPercent + "%";
//            this.getNextElement().style.width = rightInPercent + "%"

        }
     }
      Ice.PanelDivider.adjustSecondPaneSize(this.source, this.horizontal);
      this.getNextElement().style.visibility = savedVisibility;
      inPercent = inPercent + 0.01;
        this.submitInfo(event, inPercent);
  },
  
  submitInfo:function(event, inPercent) {
        var form = Ice.util.findForm(this.source);
        var clientId = this.getContainerElement().id;
        var firstPaneStyleElement  = $(clientId + "FirstPane");
        var secondPaneStyleElement  = $(clientId + "SecondPane");
        var inPercentElement  = $(clientId + "InPercent");        
        firstPaneStyleElement.value = this.getPreviousElement().style.cssText;
        secondPaneStyleElement.value = this.getNextElement().style.cssText; 
        inPercentElement.value = Math.round(inPercent * 100);
        iceSubmitPartial(form,this.source,event);
  }

});

Ice.PanelDivider.adjustSecondPaneSize = function(divider, isHorizontal) {
    divider = $(divider);
//    var container = $(Ice.PanelDivider.prototype.getContainerElement.call({source:divider})); // <ice:panelDivider>
    var container = $(divider.parentNode); // dimensions could be different from <ice:panelDivider>
    var firstPane = $(Ice.PanelDivider.prototype.getPreviousElement.call({source:divider}));
    var secondPane = $(Ice.PanelDivider.prototype.getNextElement.call({source:divider}));
    // Assuming no padding in container, no margin in divider and panes, and no padding or border in 2nd pane.
    // No way to determine their pixel values. Also, there may be margin collapsing, and
    // (offsetWidth - clientWidth) may include the scrollbar width, not just the border width.
    if (isHorizontal) {
        secondPane.style.height = container.clientHeight - firstPane.offsetHeight - divider.offsetHeight + "px";
    } else {
        // Firefox often wraps right pane around even though it should fit exactly, therefore subtract 1 more pixel.
        secondPane.style.width = container.clientWidth - firstPane.offsetWidth - divider.offsetWidth - 1 + "px";
    }
}

Ice.PanelDivider.dividerHash = $H();

Ice.PanelDivider.onWindowResize = function() {
    Ice.PanelDivider.dividerHash.each(function(pair) {
        if (!$(pair.key)) {
            Ice.PanelDivider.dividerHash.unset(pair.key);
            return;
        }
        Ice.PanelDivider.adjustSecondPaneSize(pair.key, pair.value);
    });
}

Ice.PanelDivider.onLoad = function(divider, isHorizontal) {
    Event.stopObserving(window, "resize", Ice.PanelDivider.onWindowResize); // Will register multiple times if don't do this?
    Ice.PanelDivider.dividerHash.set(divider, isHorizontal); // Will replace existing, if any.
    Event.observe(window, "resize", Ice.PanelDivider.onWindowResize);
    Ice.PanelDivider.adjustSecondPaneSize(divider, isHorizontal);
    Ice.PanelDivider.adjustPercentBasedHeight(divider, isHorizontal);
}

ResizableUtil = {
    adjustHeight:function(src) {
        var height = Element.getHeight(src);
        var paddingTop = parseInt(Element.getStyle(src, 'padding-top'));
        var paddingBottom = parseInt(Element.getStyle(src, 'padding-top'));
        if (paddingTop && paddingTop > 1) {
            height -= paddingTop; 
        }
        if (paddingBottom && paddingBottom > 1) {
            height -= paddingBottom;
        }        
        src.firstChild.style.height= (height-1) + 'px'; 
    }
}

//this function added to fix ICE-4044 (Issue when setting panelDivider to a non-fixed height )
Ice.PanelDivider.adjustPercentBasedHeight = function(divider, isHorizontal) {
    if (isHorizontal)return;         
    var rootElementId = divider.replace("Divider",""); 
    var rootElement = $(rootElementId);
    
    var rootHeight = Element.getStyle(rootElement, 'height');
    var percentBasedHeight = null;
    if (rootHeight && rootHeight.indexOf("%") > 0) {
        percentBasedHeight = rootHeight.split("%")[0];
    } 
    if (percentBasedHeight) {
        parentHeight = Ice.PanelDivider.getParentHeight(rootElement);
        newVal = Math.round(parentHeight * (percentBasedHeight / 100));
        rootElement.style.height = newVal + "px"; 
        $(divider).style.height = newVal + "px";
    }
}

//this function recusivly check the height of the parent element, until one found
//if none found and body has reached, then return the height of the viewport
Ice.PanelDivider.getParentHeight = function(element) {
     //if ture means that height is not assigned to any parent, so now get the 
     //height of the viewPort
     if (element.tagName == 'BODY') {
        var viewPortHeight = document.viewport.getHeight();
        //for opera get the window.innerHeight
        if (Prototype.Browser.WebKit && typeof window.innerHeight != 'undefined'){
            viewPortHeight = window.innerHeight;
        }       //sub 4 to avoid scrollbar
        return (viewPortHeight-4);
     }
     var sHeight = Element.getStyle(element, 'height');
     if (sHeight.indexOf("%") > 0) {
        return Ice.PanelDivider.getParentHeight(element.parentNode);
     } else {
         sHeight = Element.getHeight(element);
         //if no height defined on the element, it returns 2 without any unit
         //so get the height of its parent   
         if (sHeight == "2") {

             return Ice.PanelDivider.getParentHeight(element.parentNode);
         }
     }        
     return sHeight;
 }
