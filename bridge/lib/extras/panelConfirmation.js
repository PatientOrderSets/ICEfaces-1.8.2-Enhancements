Ice.PanelConfirmation = Class.create({
    initialize: function(trigger,e,confirmationPanelId,autoCentre,draggable,displayAtMouse,iframeUrl,handler) {
        this.srcComp = trigger;
        this.event = e;
        this.panel = $(confirmationPanelId);
        this.url = iframeUrl;
        this.srcHandler = handler;
        
        this.isAutoCentre = autoCentre;
        this.isDraggable = draggable;
        this.isAtMouse = displayAtMouse;
        
        Ice.PanelConfirmation.current = this;
        this.showPanel();
    },
    showPanel: function() {
        Ice.modal.start(this.panel.id,this.url);
        Ice.iFrameFix.start(this.panel.id,this.url);
        this.panel.style.display = '';
        this.handleDraggableObject();
        Ice.autoPosition.stop(this.panel.id);
        if (this.isAtMouse) {
            this.panel.style.left = parseInt(Event.pointerX(this.event)) + "px";
            this.panel.style.top = parseInt(Event.pointerY(this.event)) + "px"; 
        } else {
            Ice.autoCentre.start(this.panel.id);
        }
        if (!this.isAutoCentre) {
            Ice.autoCentre.stop(this.panel.id);
        }
        this.setDefaultFocus();
    },
    accept: function() {
        this.close();
        setFocus(this.srcComp.id);
        this.srcHandler.call(this.srcComp,this.event);
    },
    cancel: function() {
        this.close();
    },
    close: function() {
        Ice.PanelConfirmation.current = null;
        this.panel.style.visibility = 'hidden';
        this.panel.style.display = 'none';
        Ice.modal.stop(this.panel.id);
        Ice.autoCentre.stop(this.panel.id);
        Draggable.removeMe(this.panel.id);
        Ice.Focus.setFocus(this.srcComp.id);
    },
    handleDraggableObject: function() {
        if (this.isDraggable) {
            Ice.DnD.adjustPosition(this.panel.id);
            new Draggable(this.panel.id,{
                handle:this.panel.id+'-handle',
                dragGhost:false,
                dragCursor:false,
                ghosting:false,
                revert:false,
                mask:'1,2,3,4,5'
            });
        }
    },
    setDefaultFocus: function() {
        var cancel = $(this.panel.id + '-cancel');
        if (cancel) {
            cancel.focus();
        } else {
            $(this.panel.id + '-accept').focus();
        }
    }
});

Ice.PanelConfirmation.current = null;