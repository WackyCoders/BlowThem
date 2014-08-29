package com.blowthem.app.battle;

/**
 * Created by walter on 29.08.14.
 */
public class StaticInitials {

    private Float hp;

    private Float armor;

    private Float damage;

    private Float speed;

    public StaticInitials(Float hp, Float damage){
        this.hp = hp;
        this.damage = damage;
    }

    public Float getHp() {
        return hp;
    }

    public void setHp(Float hp) {
        this.hp = hp;
    }

    public Float getArmor() {
        return armor;
    }

    public void setArmor(Float armor) {
        this.armor = armor;
    }

    public Float getDamage() {
        return damage;
    }

    public void setDamage(Float damage) {
        this.damage = damage;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }
}
