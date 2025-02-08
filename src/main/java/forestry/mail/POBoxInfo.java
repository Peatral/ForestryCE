package forestry.mail;

import forestry.api.mail.v2.carrier.ICarrierType;

import java.util.Map;

public record POBoxInfo(Map<ICarrierType<?>, Integer> letters) {
    public boolean hasMail() {
        return letters.values().stream().anyMatch(i -> i > 0);
    }
}
