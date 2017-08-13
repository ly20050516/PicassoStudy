///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
// THIS FILE IS AUTOGENERATED, PLEASE DO NOT EDIT OR ELSE
package com.squareup.haha.trove;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * An open addressed Map implementation for float keys and Object values.
 *
 * Created: Sun Nov  4 08:52:45 2001
 *
 * @author Eric D. Friedman
 */
public class TFloatObjectHashMap<V> extends THash implements TFloatHashingStrategy {

   /** the values of the map */
   protected transient V[] _values;

   /** the set of floats */
   protected transient float[] _set;

   /** strategy used to hash values in this collection */
   protected final TFloatHashingStrategy _hashingStrategy;

   /**
     * Creates a new <code>TFloatObjectHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TFloatObjectHashMap() {
        _hashingStrategy = this;
    }

    /**
     * Creates a new <code>TFloatObjectHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TFloatObjectHashMap(int initialCapacity) {
        super(initialCapacity);
        _hashingStrategy = this;
    }

    /**
     * Creates a new <code>TFloatObjectHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TFloatObjectHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        _hashingStrategy = this;
    }

    /**
     * Creates a new <code>TFloatObjectHashMap</code> instance with the default
     * capacity and load factor.
     * @param strategy used to compute hash codes and to compare keys.
     */
    public TFloatObjectHashMap(TFloatHashingStrategy strategy) {
        _hashingStrategy = strategy;
    }

    /**
     * Creates a new <code>TFloatObjectHashMap</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     * @param strategy used to compute hash codes and to compare keys.
     */
    public TFloatObjectHashMap(int initialCapacity, TFloatHashingStrategy strategy) {
        super(initialCapacity);
        _hashingStrategy = strategy;
    }

    /**
     * Creates a new <code>TFloatObjectHashMap</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor used to calculate the threshold over which
     * rehashing takes place.
     * @param strategy used to compute hash codes and to compare keys.
     */
    public TFloatObjectHashMap(int initialCapacity, float loadFactor, TFloatHashingStrategy strategy) {
        super(initialCapacity, loadFactor);
        _hashingStrategy = strategy;
    }

    /**
     * @return a deep clone of this collection
     */
    @Override
    public TFloatObjectHashMap<V> clone() {
      TFloatObjectHashMap<V> m = (TFloatObjectHashMap<V>)super.clone();
      m._values = _values.clone();
      return m;
    }

    /**
     * @return a TFloatObjectIterator with access to this map's keys and values
     */
    public TFloatObjectIterator<V> iterator() {
        return new TFloatObjectIterator<V>(this);
    }

    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        _values = (V[]) new Object[capacity];
        _set = new float[capacity];

        return capacity;
    }

    /**
     * Searches the set for <tt>val</tt>
     *
     * @param val an <code>float</code> value
     * @return a <code>boolean</code> value
     */
    public boolean contains(float val) {
        return index(val) >= 0;
    }


    /**
     * Returns the capacity of the hash table.  This is the true
     * physical capacity, without adjusting for the load factor.
     *
     * @return the physical capacity of the hash table.
     */
    @Override
    protected int capacity() {
        return _values.length;
    }

    /**
     * Executes <tt>procedure</tt> for each element in the set.
     *
     * @param procedure a <code>TFloatProcedure</code> value
     * @return false if the loop over the set terminated because
     * the procedure returned false for some value.
     */
    public boolean forEach(TFloatProcedure procedure) {
        float[] set = _set;
        Object[] values = _values;
        for (int i = set.length; i-- > 0;) {
            if (isFull(values, i) && ! procedure.execute(set[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Inserts a key/value pair into the map.
     *
     * @param key an <code>float</code> value
     * @param value an <code>Object</code> value
     * @return the previous value associated with <tt>key</tt>,
     * or null if none was found.
     */
    public V put(float key, V value) {
        boolean wasFree = false;
        V previous = null;
        int index = insertionIndex(key);
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index -1;
            previous = unwrapNull(_values[index]);
            isNewMapping = false;
        }
        else {
            wasFree = isFree(_values, index);
        }
        _set[index] = key;
        _values[index] = wrapNull(value);
        if (isNewMapping) {
            postInsertHook(wasFree);
        }

        return previous;
    }

    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = _set.length;
        float[] oldKeys = _set;
        V[] oldVals = _values;

        _set = new float[newCapacity];
        _values = (V[]) new Object[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if(isFull(oldVals, i)) {
                float o = oldKeys[i];
                int index = insertionIndex(o);
                _set[index] = o;
                _values[index] = oldVals[i];
            }
        }
    }

    /**
     * retrieves the value for <tt>key</tt>
     *
     * @param key an <code>float</code> value
     * @return the value of <tt>key</tt> or null if no such mapping exists.
     */
    public V get(float key) {
        int index = index(key);
        return index < 0 ? null : unwrapNull(_values[index]);
    }

    private static <V> V unwrapNull(V value) {
       return value == TObjectHash.NULL ? null : value;
    }
    private static <V> V wrapNull(V value) {
       return value == null ? (V)TObjectHash.NULL : value;
    }

    /**
     * Empties the map.
     *
     */
    @Override
    public void clear() {
        super.clear();
        float[] keys = _set;
        Object[] values = _values;

        for (int i = keys.length; i-- > 0;) {
            keys[i] = (float)0;
            values[i] = null;
        }
    }

    /**
     * Deletes a key/value pair from the map.
     *
     * @param key an <code>float</code> value
     * @return an <code>Object</code> value
     */
    public V remove(float key) {
        V prev = null;
        int index = index(key);
        if (index >= 0) {
            prev = unwrapNull(_values[index]);
            removeAt(index);    // clear key,set; adjust size
        }
        return prev;
    }

    /**
     * Locates the index of <tt>val</tt>.
     *
     * @param val an <code>float</code> value
     * @return the index of <tt>val</tt> or -1 if it isn't in the set.
     */
    protected int index(float val) {
        float[] set = _set;
        Object[] values = _values;
        int length = set.length;
        int hash = _hashingStrategy.computeHashCode(val) & 0x7fffffff;
        int index = hash % length;

        if (!isFree(values, index) &&
                (isRemoved(values, index) || set[index] != val)) {
            // see Knuth, p. 529
            int probe = 1 + (hash % (length - 2));

            do {
                index -= probe;
                if (index < 0) {
                    index += length;
                }
            } while (!isFree(values, index) &&
                    (isRemoved(values, index) || set[index] != val));
        }

        return isFree(values, index) ? -1 : index;
    }

    /**
     * Locates the index at which <tt>val</tt> can be inserted.  if
     * there is already a value equal()ing <tt>val</tt> in the set,
     * returns that value as a negative integer.
     *
     * @param val an <code>float</code> value
     * @return an <code>int</code> value
     */
    protected int insertionIndex(float val) {
        Object[] values = _values;
        float[] set = _set;
        int length = set.length;
        int hash = _hashingStrategy.computeHashCode(val) & 0x7fffffff;
        int index = hash % length;

        if (isFree(values, index)) {
            return index;       // empty, all done
        }
        if (isFull(values, index) && set[index] == val) {
            return -index -1;   // already stored
        }

        // already FULL or REMOVED, must probe
        // compute the double hash
        int probe = 1 + (hash % (length - 2));
        // starting at the natural offset, probe until we find an
        // offset that isn't full.

        // keep track of the first removed cell. it's the natural candidate for re-insertion
        int firstRemoved = isRemoved(values, index) ? index : -1;

        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }
            if (firstRemoved == -1 && isRemoved(values, index)) {
                firstRemoved = index;
            }
        }
        while (isFull(values, index) && set[index] != val);

        // if the index we found was removed: continue probing until we
        // locate a free location or an element which equal()s the
        // one we have.
        if (isRemoved(values, index)) {
            while (!isFree(values, index) &&
                    (isRemoved(values, index) || set[index] != val)) {
                index -= probe;
                if (index < 0) {
                    index += length;
                }
            }
        }
        // if it's full, the key is already stored
        if (isFull(values, index)) {
            return -index -1;
        }

        return firstRemoved == -1 ? index : firstRemoved;
    }

    static boolean isFull(Object[] values, int index) {
        Object value = values[index];
        return value != null && value != TObjectHash.REMOVED;
    }

    private static boolean isRemoved(Object[] values, int index) {
        return values[index] == TObjectHash.REMOVED;
    }

    private static boolean isFree(Object[] values, int index) {
        return values[index] == null;
    }

    /**
     * Compares this map with another map for equality of their stored
     * entries.
     *
     * @param other an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(Object other) {
        if (! (other instanceof TFloatObjectHashMap)) {
            return false;
        }
        TFloatObjectHashMap that = (TFloatObjectHashMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        return forEachEntry(new EqProcedure<V>(that));
    }

    public int hashCode() {
        HashProcedure p = new HashProcedure();
        forEachEntry(p);
        return p.getHashCode();
    }

    private final class HashProcedure implements TFloatObjectProcedure<V> {
        private int h;

        HashProcedure() {
        }

        public int getHashCode() {
            return h;
        }

        @Override
        public final boolean execute(float key, V value) {
            h += _hashingStrategy.computeHashCode(key) ^ HashFunctions.hash(value);
            return true;
        }
    }

    private static final class EqProcedure<V> implements TFloatObjectProcedure<V> {
        private final TFloatObjectHashMap<V> _otherMap;

        EqProcedure(TFloatObjectHashMap<V> otherMap) {
            _otherMap = otherMap;
        }

        @Override
        public final boolean execute(float key, V value) {
            int index = _otherMap.index(key);
            return index >= 0 && eq(value, _otherMap.get(key));
        }

        /**
         * Compare two objects for equality.
         */
        private static boolean eq(Object o1, Object o2) {
            return o1 == o2 || o1 != null && o1.equals(o2);
        }
    }

    /**
     * removes the mapping at <tt>index</tt> from the map.
     *
     * @param index an <code>int</code> value
     */
    @Override
    protected void removeAt(int index) {
        _values[index] = (V)TObjectHash.REMOVED;
        super.removeAt(index);  // clear key, set; adjust size
    }

    /**
     * Returns the values of the map.
     *
     * @return a value <code>array</code>
     */
    public Object[] getValues() {
        Object[] vals = new Object[size()];
        V[] values = _values;

        for (int i = values.length, j = 0; i-- > 0;) {
          if (isFull(values, i)) {
            vals[j++] = unwrapNull(values[i]);
          }
        }
        return vals;
    }

    /**
     * returns the keys of the map.
     *
     * @return a <code>Set</code> value
     */
    public float[] keys() {
        float[] keys = new float[size()];
        float[] k = _set;
        Object[] values = _values;

        for (int i = k.length, j = 0; i-- > 0;) {
          if (isFull(values, i)) {
            keys[j++] = k[i];
          }
        }
        return keys;
    }

    /**
     * checks for the presence of <tt>val</tt> in the values of the map.
     *
     * @param val an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean containsValue(V val) {
        V[] values = _values;

        // special case null values so that we don't have to
        // perform null checks before every call to equals()
        if (null == val) {
            for (int i = values.length; i-- > 0;) {
                if (TObjectHash.NULL == values[i]) {
                    return true;
                }
            }
        }
        else {
            for (int i = values.length; i-- > 0;) {
                V value = unwrapNull(values[i]);
                if (isFull(values, i) && (val == value || val.equals(value))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks for the present of <tt>key</tt> in the keys of the map.
     *
     * @param key an <code>float</code> value
     * @return a <code>boolean</code> value
     */
    public boolean containsKey(float key) {
        return contains(key);
    }

    /**
     * Executes <tt>procedure</tt> for each key in the map.
     *
     * @param procedure a <code>TFloatProcedure</code> value
     * @return false if the loop over the keys terminated because
     * the procedure returned false for some key.
     */
    public boolean forEachKey(TFloatProcedure procedure) {
        return forEach(procedure);
    }

    /**
     * Executes <tt>procedure</tt> for each value in the map.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forEachValue(TObjectProcedure<V> procedure) {
        V[] values = _values;
        for (int i = values.length; i-- > 0;) {
            if (isFull(values, i) && ! procedure.execute(unwrapNull(values[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes <tt>procedure</tt> for each key/value entry in the
     * map.
     *
     * @param procedure a <code>TOFloatObjectProcedure</code> value
     * @return false if the loop over the entries terminated because
     * the procedure returned false for some entry.
     */
    public boolean forEachEntry(TFloatObjectProcedure<V> procedure) {
        float[] keys = _set;
        V[] values = _values;
        for (int i = keys.length; i-- > 0;) {
            if (isFull(values, i) && ! procedure.execute(keys[i],unwrapNull(values[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retains only those entries in the map for which the procedure
     * returns a true value.
     *
     * @param procedure determines which entries to keep
     * @return true if the map was modified.
     */
    public boolean retainEntries(TFloatObjectProcedure<V> procedure) {
        boolean modified = false;
        float[] keys = _set;
        V[] values = _values;
        stopCompactingOnRemove();
        try {
            for (int i = keys.length; i-- > 0;) {
                if (isFull(values, i) && ! procedure.execute(keys[i],unwrapNull(values[i]))) {
                    removeAt(i);
                    modified = true;
                }
            }
        }
        finally {
            startCompactingOnRemove(modified);
        }
        return modified;
    }

    /**
     * Transform the values in this map using <tt>function</tt>.
     *
     * @param function a <code>TObjectFunction</code> value
     */
    public void transformValues(TObjectFunction<V,V> function) {
        V[] values = _values;
        for (int i = values.length; i-- > 0;) {
            if (isFull(values, i)) {
                values[i] = wrapNull(function.execute(unwrapNull(values[i])));
            }
        }
    }



    private void writeObject(ObjectOutputStream stream)
        throws IOException {
        stream.defaultWriteObject();

        // number of entries
        stream.writeInt(_size);

        SerializationProcedure writeProcedure = new SerializationProcedure(stream);
        if (! forEachEntry(writeProcedure)) {
            throw writeProcedure.exception;
        }
    }

    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        int size = stream.readInt();
        setUp(size);
        while (size-- > 0) {
            float key = stream.readFloat();
            V val = (V) stream.readObject();
            put(key, val);
        }
    }

    /**
     * Default implementation of TFloatHashingStrategy:
     * delegates hashing to HashFunctions.hash(float).
     *
     * @param val the value to hash
     * @return the hashcode.
     */
    @Override
    public final int computeHashCode(float val) {
        return HashFunctions.hash(val);
    }

} // TFloatObjectHashMap
