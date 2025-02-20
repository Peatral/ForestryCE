/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INbtWritable;

public interface IMailAddress extends INbtWritable {

	IPostalCarrier getCarrier();

	String getName();

	boolean isValid();

	GameProfile getPlayerProfile();
}
