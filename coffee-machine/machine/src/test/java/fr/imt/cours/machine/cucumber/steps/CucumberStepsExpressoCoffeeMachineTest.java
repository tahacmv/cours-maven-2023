package fr.imt.cours.machine.cucumber.steps;

import fr.imt.cours.machine.EspressoCoffeeMachine;
import fr.imt.cours.machine.exception.CoffeeTypeCupDifferentOfCoffeeTypeTankException;
import fr.imt.cours.machine.exception.LackOfWaterInTankException;
import fr.imt.cours.machine.exception.MachineNotPluggedException;
import fr.imt.cours.storage.coffeeType.CoffeeType;
import fr.imt.cours.storage.container.*;
import fr.imt.cours.storage.exception.CannotMakeCremaWithSimpleCoffeeMachine;
import fr.imt.cours.storage.exception.CupNotEmptyException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class CucumberStepsExpressoCoffeeMachineTest {

    public EspressoCoffeeMachine espressoMachine;
    public Mug espressoMug;
    public Cup espressoCup;
    public CoffeeContainer espressoContainerWithCoffee;


    @Given("a espresso machine with {double} l of min capacity, {double} l of max capacity, {double} l per h of water flow for the pump")
    public void givenAEspressoMachine(double minimalWaterCapacity, double maximalWaterCapacity, double pumpWaterFlow){
        espressoMachine = new EspressoCoffeeMachine(minimalWaterCapacity, maximalWaterCapacity, minimalWaterCapacity, maximalWaterCapacity, pumpWaterFlow);
    }

    @Given("a espresso machine with {double} l of min capacity, {double} l of max capacity, {double} l per h of water flow for the pump that is out of order")
    public void givenAEspressoMachineOutOfOrder(double minimalWaterCapacity, double maximalWaterCapacity, double pumpWaterFlow){
        espressoMachine = new EspressoCoffeeMachine(minimalWaterCapacity, maximalWaterCapacity, minimalWaterCapacity, maximalWaterCapacity, pumpWaterFlow);
        espressoMachine.setOutOfOrder(true);
    }

    @And("a espresso {string} with a capacity of {double}")
    public void aWithACapacityOf(String containerType, double containerCapacity) {
        if ("mug".equals(containerType))
            espressoMug = new Mug(containerCapacity);
        if ("cup".equals(containerType))
            espressoCup = new Cup(containerCapacity);
    }

    @When("I plug the espresso machine to electricity")
    public void iPlugTheMachineToElectricityEspresso() {
        espressoMachine.plugToElectricalPlug();
    }

    @And("I add {double} liter of water in the espresso machine water tank")
    public void iAddLitersOfWaterEspresso(double waterVolume) {
        espressoMachine.addWaterInTank(waterVolume);
    }

    @And("I add {double} liter of {string} in the espresso machine bean tank")
    public void iAddLitersOfCoffeeBeansEspresso(double beanVolume, String coffeeType) {
        espressoMachine.addCoffeeInBeanTank(beanVolume, CoffeeType.valueOf(coffeeType));
    }

    @And("I made a coffee {string} in the espresso machine")
    public void iMadeAEspresso(String coffeeType) throws InterruptedException, CupNotEmptyException, LackOfWaterInTankException, MachineNotPluggedException, CoffeeTypeCupDifferentOfCoffeeTypeTankException, CannotMakeCremaWithSimpleCoffeeMachine, fr.imt.cours.storage.exception.CoffeeTypeCupDifferentOfCoffeeTypeTankException, fr.imt.cours.storage.exception.LackOfWaterInTankException, fr.imt.cours.storage.exception.MachineNotPluggedException {

        if (espressoMug != null)
            espressoContainerWithCoffee = espressoMachine.makeACoffee(espressoMug, CoffeeType.valueOf(coffeeType));
        if (espressoCup != null)
            espressoContainerWithCoffee = espressoMachine.makeACoffee(espressoCup, CoffeeType.valueOf(coffeeType));

    }

    @Then("the espresso machine return a espresso mug not empty")
    public void theEspressoMachineReturnACoffeeMugNotEmpty() {
        Assertions.assertFalse(espressoContainerWithCoffee.isEmpty());
    }



    @And("a espresso volume equals to {double}")
    public void aEspressoVolumeEqualsTo(double coffeeVolume) {
        assertThat(coffeeVolume, is(espressoContainerWithCoffee.getCapacity()));
    }

    @And("a espresso {string} containing a espresso type {string}")
    public void aCoffeeMugContainingACoffeeTypeEspresso(String containerType, String coffeeType) {
        if ("mug".equals(containerType))
            assertThat(espressoContainerWithCoffee, instanceOf(CoffeeMug.class));
        if ("cup".equals(containerType))
            assertThat(espressoContainerWithCoffee, instanceOf(CoffeeCup.class));

        assertThat(espressoContainerWithCoffee.getCoffeeType(), is(CoffeeType.valueOf(coffeeType)));
    }

    @Then("the espresso machine is plugged")
    public void theEspressoMachineIsPlugged(){
        Assertions.assertTrue(espressoMachine.isPlugged());
    }

    @And("the espresso machine water tank contains {double} l of water")
    public void theEspressoMachineContainsWater(double waterAdded){
        Assertions.assertEquals(waterAdded, espressoMachine.getWaterTank().getActualVolume());
    }

}
