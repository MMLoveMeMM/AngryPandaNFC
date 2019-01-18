package cn.pumpkin.angrypandanfc.data;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/18 09:36
 * @des:
 * @see {@link }
 */

public class Word {
    public String name;
    public String gender;
    public String nation;
    public String birthday;
    public String address;
    public String idCard;
    public String issuingAuthority;
    public String startTime;
    public String startopTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartopTime() {
        return startopTime;
    }

    public void setStartopTime(String startopTime) {
        this.startopTime = startopTime;
    }

    @Override
    public String toString() {
        return "Word{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", nation='" + nation + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", idCard='" + idCard + '\'' +
                ", issuingAuthority='" + issuingAuthority + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startopTime='" + startopTime + '\'' +
                '}';
    }
}
