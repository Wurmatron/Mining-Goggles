package io.wurmatron.mining_goggles.config.wrapper;

public class OreWavelength {

  public String ore;
  public int optimalWavelength;
  public int tuning;

  public OreWavelength() {
  }

  public OreWavelength(String ore, int optimalWavelength, int tuning) {
    this.ore = ore;
    this.optimalWavelength = optimalWavelength;
    this.tuning = tuning;
  }
}
