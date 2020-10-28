package cs203.g1t7.asset;

import java.io.Serializable;

public class AssetId implements Serializable{
    private int customer_id;
    private String code;

    public AssetId(int customer_id, String code) {
        this.customer_id = customer_id;
        this.code = code;
    }

    public AssetId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetId temp = (AssetId) o;
        return customer_id == temp.customer_id && code.equals(temp.code);
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(customer_id);
        for (char ch : code.toCharArray()) sb.append((byte) ch);
        return Integer.parseInt(sb.toString());
    }
}