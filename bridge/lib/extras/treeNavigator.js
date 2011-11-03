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
Ice.treeNavigator = Class.create();
Ice.treeNavigator = {
  
      handleFocus: function (event, root, deep) {
           var type = event.type;
           if(type == 'click') {
              Ice.treeNavigator.reset();
              return;
           }

           var ele = Event.element(event);
           var kc= event.keyCode;
           var imgSrc = null;
           if (ele && ele.firstChild.getAttribute) {
              imgSrc = ele.firstChild.getAttribute('src');
           }
          if (!imgSrc) return;
          switch (kc) {
	          case 37: //left
	          //root node
	          if (imgSrc.indexOf('top_close_no_siblings') > 0 ||
	          imgSrc.indexOf('middle_close') > 0 ||
	          imgSrc.indexOf('bottom_close') > 0 ) {
	            logger.info('LEFT_KEY: top_close_no_siblings FOUND, root Node opend close it and reinitialize index'); 
	            ele.onclick();
	            Ice.treeNavigator.reset();
                return false;
	          }    
	    
	          break;
	
	          case 39: //right
		          if (imgSrc.indexOf('top_open_no_siblings') > 0 ||
		          imgSrc.indexOf('middle_open') > 0 ||
		          imgSrc.indexOf('bottom_open') > 0 ) {
		                ele.onclick();
		                Ice.treeNavigator.reset();  
		                return false;             
		          }        
	                
	          break;
	
	          case 38: //up
	              if (!Ice.treeNavigator.anchors) {
		             Ice.treeNavigator.updateAnchor(root, ele, deep);
		          }
		
		          if (imgSrc) {
		             if(imgSrc.indexOf('top_close_no_siblings') > 0) {
			            Ice.treeNavigator.index = 1;
			            Ice.treeNavigator.anchors[Ice.treeNavigator.index].focus();
		             }
		            Ice.treeNavigator.focusPrevious();
		          }  else {
		            Ice.treeNavigator.focusPrevious();
		          }
	          return false;              
	          case 40: //down
	              logger.info ('down'); 
	              if (!Ice.treeNavigator.anchors) {
	                Ice.treeNavigator.updateAnchor(root, ele, deep);
	              }
	              Ice.treeNavigator.focusNext();
	          return false;
          }//switch ends    
      }, //func ends
  
          index:0,
          
          anchors:null,
          
          reset: function() {
             Ice.treeNavigator.index = 0;
             Ice.treeNavigator.anchors = null;     
      },
      
      focusNext: function(deep) {  
         if (Ice.treeNavigator.index <(Ice.treeNavigator.anchors.length-1)){
             Ice.treeNavigator.index = Ice.treeNavigator.index + 1;
         }
         Ice.treeNavigator.anchors[Ice.treeNavigator.index].focus();
      },
      
      focusPrevious : function(deep) {
         if (Ice.treeNavigator.index>0) {
             Ice.treeNavigator.index = Ice.treeNavigator.index - 1;
         }
         Ice.treeNavigator.anchors[Ice.treeNavigator.index].focus();      
      },
      
      updateAnchor: function(root, ele, deep) {
        var anchors = [];
            if(deep) { 
                anchors = root.parentNode.getElementsByTagName('a');
                for (i=0; anchors.length > i; i++) {
                    if (ele == anchors[i]) {
                        Ice.treeNavigator.index = i;   
                    }
                }//for                  
            } else {   
	            _anchors = root.parentNode.getElementsByTagName('a');
	            j = 0;
	            for (i=0; _anchors.length > i; i++) {
	               if (_anchors[i].firstChild.src && _anchors[i].firstChild.src.indexOf('tree_nav') > 0) {
	                    if (ele == _anchors[i]) {
	                       Ice.treeNavigator.index = j;     
	                    }
	                    anchors[j++] = _anchors[i];
	               }
	            }//for    
            }
            Ice.treeNavigator.anchors = anchors;
         }//updateAnchor
  }// func ends