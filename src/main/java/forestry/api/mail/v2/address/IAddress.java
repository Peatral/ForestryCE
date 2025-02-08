package forestry.api.mail.v2.address;

import forestry.api.mail.v2.carrier.ICarrierType;

public interface IAddress<R> {
    R addressee();
    ICarrierType<R> carrier();

    default String getAddresseeName() {
        return carrier().getAddresseeName(this);
    }
}
