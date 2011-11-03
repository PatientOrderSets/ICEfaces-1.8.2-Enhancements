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

/*
 * Created on May 18, 2005
 */
package com.icesoft.faces.context;

import java.util.*;

public abstract class AbstractAttributeMap implements Map {

    private Collection values;
    private Set keySet;
    private Set entrySet;

    /* (
      * @see java.util.Map#size()
      */
    public int size() {
        int size = 0;
        Enumeration e = getAttributeNames();
        while (e.hasMoreElements()) {
            e.nextElement();
            size++;
        }
        return size;
    }

    /*
      * @see java.util.Map#clear()
      */
    public void clear() {
        Enumeration e = getAttributeNames();
        List keys = new ArrayList();
        while (e.hasMoreElements())
            keys.add(e.nextElement());

        Iterator iterator = keys.iterator();
        while (iterator.hasNext())
            removeAttribute((String) iterator.next());
    }

    /*
      * @see java.util.Map#isEmpty()
      */
    public boolean isEmpty() {
        return !getAttributeNames().hasMoreElements();
    }

    /* (non-Javadoc)
      * @see java.util.Map#containsKey(java.lang.Object)
      */
    public boolean containsKey(Object key) {
        return getAttribute(key.toString()) != null;
    }

    /* (non-Javadoc)
      * @see java.util.Map#containsValue(java.lang.Object)
      */
    public boolean containsValue(Object value) {
        if (value == null)
            return false;

        Enumeration e = getAttributeNames();
        while (e.hasMoreElements()) {
            if (value.equals(getAttribute(e.nextElement().toString()))) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
      * @see java.util.Map#values()
      */
    public Collection values() {
        return (values != null) ? values : (values = new Values());
    }

    /* (non-Javadoc)
      * @see java.util.Map#putAll(java.util.Map)
      */
    public void putAll(Map map) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            setAttribute(entry.getKey().toString(), entry.getValue());
        }
    }

    /* (non-Javadoc)
      * @see java.util.Map#entrySet()
      */
    public Set entrySet() {
        return (entrySet != null) ? entrySet : (entrySet = new EntrySet());
    }

    /* (non-Javadoc)
      * @see java.util.Map#keySet()
      */
    public Set keySet() {
        return (keySet != null) ? keySet : (keySet = new KeySet());
    }

    /* (non-Javadoc)
      * @see java.util.Map#get(java.lang.Object)
      */
    public Object get(Object key) {
        return getAttribute(key.toString());
    }

    /*
      * @see java.util.Map#remove(java.lang.Object)
      */
    public Object remove(Object key) {
        Object associatedValue = getAttribute(key.toString());
        removeAttribute(key.toString());
        return associatedValue;
    }

    /* (non-Javadoc)
      * @see java.util.Map#put(java.lang.Object, java.lang.Object)
      */
    public Object put(Object key, Object value) {
        Object previousValue = getAttribute(key.toString());
        setAttribute(key.toString(), value);
        return previousValue;
    }

    abstract protected Object getAttribute(String key);

    abstract protected void setAttribute(String key, Object value);

    abstract protected void removeAttribute(String key);

    abstract protected Enumeration getAttributeNames();

    private class KeySet extends AbstractSet {

        /*
           * @see java.util.AbstractCollection#iterator()
           */
        public Iterator iterator() {
            return new KeyIterator();
        }

        /*
           * @see java.util.AbstractCollection#size()
           */
        public int size() {
            return AbstractAttributeMap.this.size();
        }

        public boolean isEmpty() {
            return AbstractAttributeMap.this.isEmpty();
        }

        public boolean contains(Object key) {
            return AbstractAttributeMap.this.containsKey(key);
        }

        public boolean remove(Object key) {
            return AbstractAttributeMap.this.remove(key) != null;
        }

        public void clear() {
            AbstractAttributeMap.this.clear();
        }
    }

    private class KeyIterator implements Iterator {

        protected final Enumeration e = getAttributeNames();
        protected Object currentKey;

        /*
           * @see java.util.Iterator#hasNext()
           */
        public boolean hasNext() {
            return e.hasMoreElements();
        }

        /*
           * @see java.util.Iterator#next()
           */
        public Object next() {
            return currentKey = e.nextElement();
        }

        /*
           * @see java.util.Iterator#remove()
           */
        public void remove() {
            if (currentKey == null) {
                throw new NoSuchElementException(
                        "No element is pointed for remove operation");
            }
            AbstractAttributeMap.this.remove(currentKey);
        }
    }

    private class Values extends AbstractCollection {

        public Iterator iterator() {
            return new ValuesIterator();
        }

        public boolean contains(Object value) {
            return containsValue(value);
        }

        public boolean remove(Object value) {
            if (value == null)
                return false;

            Iterator iterator = iterator();
            while (iterator.hasNext()) {
                if (value.equals(iterator.next())) {
                    iterator.remove();
                    return true;
                }
            }
            return false;
        }

        public int size() {
            return AbstractAttributeMap.this.size();
        }

        public boolean isEmpty() {
            return AbstractAttributeMap.this.isEmpty();
        }

        public void clear() {
            AbstractAttributeMap.this.clear();
        }
    }

    private class ValuesIterator extends KeyIterator {
        public Object next() {
            super.next();
            return AbstractAttributeMap.this.get(currentKey);
        }
    }

    private class EntrySet extends KeySet {
        public Iterator iterator() {
            return new EntryIterator();
        }

        public boolean contains(Object object) {
            if (object == null)
                return false;

            if (!(object instanceof Entry))
                return false;

            Entry entry = (Entry) object;
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key == null || value == null)
                return false;

            return value.equals(AbstractAttributeMap.this.get(key));
        }

        public boolean remove(Object object) {
            if (object == null)
                return false;

            if (!(object instanceof Entry))
                return false;

            Entry entry = (Entry) object;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null || !value.equals(AbstractAttributeMap.this.get(key)))
                return false;

            return AbstractAttributeMap.this.remove(key) != null;
        }
    }

    private class EntryIterator extends KeyIterator {
        public Object next() {
            super.next();
            return new EntrySetEntry(currentKey);
        }
    }

    private class EntrySetEntry implements Entry {
        private final Object currentKey;

        public EntrySetEntry(Object currentKey) {
            this.currentKey = currentKey;
        }

        public Object getKey() {
            return currentKey;
        }

        public Object getValue() {
            return AbstractAttributeMap.this.get(currentKey);
        }

        public Object setValue(Object value) {
            return AbstractAttributeMap.this.put(currentKey, value);
        }
    }
}
