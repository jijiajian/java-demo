package cn.jijiajian.redisdemo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author J
 * @time 2017/10/29 22:30
 * @description
 **/
public class DemoEntity implements Serializable {


    private Integer id;
    private String name;
    private Integer age;
    private String address;
    private BigDecimal money;
    private InnerEntity innerEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public InnerEntity getInnerEntity() {
        return innerEntity;
    }

    public void setInnerEntity(InnerEntity innerEntity) {
        this.innerEntity = innerEntity;
    }

    @Override
    public String toString() {
        return "{" + id + "," + name + "}";
    }
}
