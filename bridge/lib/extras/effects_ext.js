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
// Original copyright and license
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
// Contributors:
//  Justin Palmer (http://encytemedia.com/)
//  Mark Pilgrim (http://diveintomark.org/)
//  Martin Bialasinki
// 
// See scriptaculous.js for full license.  

Effect.Highlight.prototype.ORIGINAL_setup = Effect.Highlight.prototype.setup;
Effect.Highlight.prototype.setup = function() {
    if (this.element.highlighting) {
        this.cancel();
        return;
    }
    this.ORIGINAL_setup();
    this.element.highlighting = true;
}

Effect.Highlight.prototype.ORIGINAL_finish = Effect.Highlight.prototype.finish;
Effect.Highlight.prototype.finish = function() {
    this.ORIGINAL_finish();
    this.element.highlighting = false;
}


Object.extend(Effect.DefaultOptions, {afterFinish:function(ele) {
    if (this.uploadCSS != null) {
        Ice.DnD.StyleReader.upload(ele.element, ele.options.submit);
    }
    if (ele.options.iceFinish)
        ele.options.iceFinish(ele);
}});


function blankEffect() {
}// Blank Effect, used as a place holder in local effects

Effect.Grow = function(element) {
    element = $(element);
    var options = Object.extend({
        direction: 'center',
        moveTransition: Effect.Transitions.sinoidal,
        scaleTransition: Effect.Transitions.sinoidal,
        opacityTransition: Effect.Transitions.full
    }, arguments[1] || {});
    var oldStyle = {
        top: element.style.top,
        left: element.style.left,
        height: element.style.height,
        width: element.style.width,
        opacity: element.getInlineOpacity() };

    var dims = element.getDimensions();
    var initialMoveX, initialMoveY;
    var moveX, moveY;

    switch (options.direction) {
        case 'top-left':
            initialMoveX = initialMoveY = moveX = moveY = 0;
            break;
        case 'top-right':
            initialMoveX = dims.width;
            initialMoveY = moveY = 0;
            moveX = -dims.width;
            break;
        case 'bottom-left':
            initialMoveX = moveX = 0;
            initialMoveY = dims.height;
            moveY = -dims.height;
            break;
        case 'bottom-right':
            initialMoveX = dims.width;
            initialMoveY = dims.height;
            moveX = -dims.width;
            moveY = -dims.height;
            break;
        case 'center':
            initialMoveX = dims.width / 2;
            initialMoveY = dims.height / 2;
            moveX = -dims.width / 2;
            moveY = -dims.height / 2;
            break;
    }

    return new Effect.Move(element, {
        x: initialMoveX,
        y: initialMoveY,
        duration: 0.01,
        beforeSetup: function(effect) {
            effect.element.hide().makeClipping().makePositioned();
        },
        afterFinishInternal: function(effect) {
            new Effect.Parallel(
                    [ new Effect.Opacity(effect.element, { sync: true, to: 1.0, from: 0.0, transition: options.opacityTransition }),
                            new Effect.Move(effect.element, { x: moveX, y: moveY, sync: false, transition: options.moveTransition }),
                            new Effect.Scale(effect.element, 100, {
                                scaleMode: { originalHeight: dims.height, originalWidth: dims.width },
                                sync: false, scaleFrom: window.opera ? 1 : 0, transition: options.scaleTransition, restoreAfterFinish: true})
                            ], Object.extend({
                beforeSetup: function(effect) {
                    effect.effects[0].element.setStyle({height: '10px'}, {width: '10px'}).show();
                },
                afterFinishInternal: function(effect) {
                    effect.effects[0].element.undoClipping().undoPositioned().setStyle(oldStyle);
                }
            }, options))
        }
    });
}