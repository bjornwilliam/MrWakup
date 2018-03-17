package com.riftlabs.communicationlib.api.datatypes;

public abstract class KickBasePresetEffect {
	private KickEffectID kickEffectID;
	private String effectName;
	
	public KickBasePresetEffect(String effectName){
		this.effectName = effectName;
	}
	
	public KickEffectID getKickEffectID() {
		return kickEffectID;
	}

	public void setKickEffectID(KickEffectID kickEffectID) {
		this.kickEffectID = kickEffectID;
	}

	public String getEffectName() {
		return effectName;
	}
}
