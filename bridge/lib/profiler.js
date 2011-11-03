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

[ Ice.Profiling = new Object ].as(function(This) {

    This.MeasurementPoint = Object.subclass({
        initialize: function(name) {
            this.listener = Function.NOOP;
            this.name = name;
            this.counter = 0;
            this.totalValue = 0;
        },

        start: function() {
            this.startTime = new Date;
        },

        stop: function() {
            ++this.counter;
            var endTime = new Date;
            var lastValue = endTime.getTime() - this.startTime.getTime();
            this.totalValue += lastValue;
            var averageValue = this.totalValue / this.counter;
            this.listener(this.counter, lastValue, averageValue, this.totalValue);
        },

        onChange: function(listener) {
            this.listener = listener;
        }
    });

    This.Profiler = Object.subclass({
        initialize: function() {
            this.measurementPoints = [];
        },

        measure: function(name, method) {
            var point = new This.MeasurementPoint(name);
            this.measurementPoints.push(point);

            return function() {
                point.start();
                var result = method.apply(this, arguments);
                point.stop();
                return result;
            };
        },

        enable: function() {
            this.window = window.open('', 'profiler', 'scrollbars=1,width=600,height=300');
            var doc = this.window.document;
            this.log = doc.body.appendChild(doc.createElement('table'))
            with (this.log.style) {
                borderWidth = '1px';
                borderStyle = 'solid';
                borderColor = '#999';
                backgroundColor = '#ddd';
                overflow = 'scroll';
                fontFamily = 'Monospace';
            }

            var tableBody = this.log.appendChild(doc.createElement('tbody'));
            var header = tableBody.appendChild(doc.createElement('tr'));
            with (header.style) {
                borderBottomWidth = '1px';
                textAlign = 'left';
                textDecoration = 'underline';
                textAlign = 'left';
            }

            var nameHeader = header.appendChild(doc.createElement('th')).appendChild(doc.createTextNode('Measurement'));
            var countHeader = header.appendChild(doc.createElement('th')).appendChild(doc.createTextNode('# of Calls'));
            var averageValueHeader = header.appendChild(doc.createElement('th')).appendChild(doc.createTextNode('Average Time'));
            var totalValueHeader = header.appendChild(doc.createElement('th')).appendChild(doc.createTextNode('Total Time'));
            var lastValueHeader = header.appendChild(doc.createElement('th')).appendChild(doc.createTextNode('Time'));

            //sort alphabetically by mesurement point name
            this.measurementPoints.sort(function(pointA, pointB) {
                var a = pointA.name;
                var b = pointB.name;
                return a < b ? -1 : a > b ? 1 : 0;
            });
            //link measurement point updates the DOM nodes
            this.measurementPoints.each(function(measurementPoint) {
                var row = tableBody.appendChild(doc.createElement('tr'));
                var nameCell = row.appendChild(doc.createElement('td')).appendChild(doc.createTextNode('-'));
                var countCell = row.appendChild(doc.createElement('td')).appendChild(doc.createTextNode('-'));
                var averageValueCell = row.appendChild(doc.createElement('td')).appendChild(doc.createTextNode('-'));
                var totalValueCell = row.appendChild(doc.createElement('td')).appendChild(doc.createTextNode('-'));
                var lastValueCell = row.appendChild(doc.createElement('td')).appendChild(doc.createTextNode('-'));

                nameCell.data = measurementPoint.name;
                measurementPoint.onChange(function(count, lastValue, averageValue, totalValue) {
                    countCell.data = count.toString();
                    averageValueCell.data = Math.ceil(averageValue).toString() + ' ms';
                    totalValueCell.data = Math.ceil(totalValue).toString() + ' ms';
                    lastValueCell.data = lastValue.toString() + ' ms';
                });
            }.bind(this));

            this.window.onunload = function() {
                this.disable();
            }.bind(this);
        },

        disable: function() {
            if (this.window) {
                if (this.window.document) this.window.document.body.removeChild(this.log);
                try {
                    this.window.close();
                } catch (e) {
                    //do nothing
                }
            }
        }
    });
});

var profiler = new Ice.Profiling.Profiler();

window.onLoad(function() {
    profiler.enable();
});

window.onUnload(function() {
    profiler.disable();
});

