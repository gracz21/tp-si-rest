package com.example.rest.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author Kamil Walkowiak
 */
@Entity("counters")
public class Counter {
    @Id
    private String _id;

    private long seq;

    public Counter() {
    }

    public Counter(String _id) {
        this._id = _id;
        this.seq = 0;
    }

    public String get_id() {
        return _id;
    }

    public long getSeq() {
        return seq;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
