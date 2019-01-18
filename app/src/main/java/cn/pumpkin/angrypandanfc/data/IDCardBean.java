package cn.pumpkin.angrypandanfc.data;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/18 09:36
 * @des:
 * @see {@link }
 */

public class IDCardBean {

    public Word word;
    public byte[] headImage;

    public IDCardBean(){
        word=new Word();
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public byte[] getHeadImage() {
        return headImage;
    }

    public void setHeadImage(byte[] headImage) {
        this.headImage = headImage;
    }

    @Override
    public String toString() {
        return "IDCardBean{" +
                "word=" + word.toString() +
                ", headImage=" + headImage +
                '}';
    }
}
