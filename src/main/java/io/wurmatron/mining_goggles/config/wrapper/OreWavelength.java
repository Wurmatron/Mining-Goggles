package io.wurmatron.mining_goggles.config.wrapper;

public class OreWavelength {

  public String ore;
  public int optimalWavelength;

  public OreWavelength() {
  }

  public OreWavelength(String ore, int wavelength) {
    this.ore = ore;
    this.optimalWavelength = wavelength;
  }
}
