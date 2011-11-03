
Ice.Calendar = {};
Ice.Calendar.listeners = {};

Ice.Calendar.addCloseListener = function(calendar,form,commandLink,hiddenField) {
    if (Ice.Calendar.listeners[calendar]) {
        return;
    } else {
        Ice.Calendar.listeners[calendar] = new Ice.Calendar.CloseListener(calendar,form,commandLink,hiddenField);
    }
};

Ice.Calendar.CloseListener = Class.create({
    initialize: function(calendar,form,commandLink,hiddenField) {
        this.calendarId = calendar;
        this.formId = form;
        this.commandLinkId = commandLink;
        this.hiddenFieldId = hiddenField;
        
        this.popupId = this.calendarId + '_ct';
        this.buttonId = this.calendarId + '_cb'
        
        this.handler = this.closePopupOnClickOutside.bindAsEventListener(this);
        Event.observe(document,'mousedown',this.handler);
    },
    closePopupOnClickOutside: function(event) {
        if (this.getPopup()) {
            if (this.isInPopup(event.element())) {
                return;
            }
            if (this.isWithin(this.getPopup(),event)) {
                return;
            }
            if (event.element() == this.getButton()) {
                this.dispose();
                return;
            }
                
            var id = event.element().id;
            if (id) setFocus(id);
            else setFocus('');
            
            this.submit(event);
            this.dispose();
        }
    },
    isInPopup: function(element) {
        if (element.id == this.popupId) return true;
        if (element == undefined || element == document) return false;
        return this.isInPopup(element.parentNode);
    },
    isWithin: function(element,event) {
        return Position.within(element, Event.pointerX(event), Event.pointerY(event));
    },
    dispose: function() {
        Ice.Calendar.listeners[this.calendarId] = null;
        Event.stopObserving(document,'mousedown',this.handler);
    },
    submit: function(event) {
        document.forms[this.formId][this.commandLinkId].value=this.getButton().id;
        document.forms[this.formId][this.hiddenFieldId].value='toggle';
        iceSubmitPartial(document.forms[this.formId],this.getButton(),event);
    },
    getPopup: function() {
        return $(this.popupId);
    },
    getButton: function() {
        return $(this.buttonId);
    }
});