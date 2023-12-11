package fr.imt.coffee.machine;

import fr.imt.coffee.machine.component.ElectricalResistance;
import fr.imt.coffee.machine.component.WaterPump;
import fr.imt.coffee.machine.exception.CannotMakeCremaWithSimpleCoffeeMachine;
import fr.imt.coffee.machine.exception.CoffeeTypeCupDifferentOfCoffeeTypeTankException;
import fr.imt.coffee.machine.exception.LackOfWaterInTankException;
import fr.imt.coffee.machine.exception.MachineNotPluggedException;
import fr.imt.coffee.storage.cupboard.coffee.type.CoffeeType;
import fr.imt.coffee.storage.cupboard.container.CoffeeContainer;
import fr.imt.coffee.storage.cupboard.container.CoffeeCup;
import fr.imt.coffee.storage.cupboard.container.CoffeeMug;
import fr.imt.coffee.storage.cupboard.container.Cup;
import fr.imt.coffee.storage.cupboard.container.Mug;
import fr.imt.coffee.storage.cupboard.exception.CupNotEmptyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CoffeeMachineUnitTest {
    public CoffeeMachine coffeeMachineUnderTest;

    /**
     * @BeforeEach est une annotation permettant d'exécuter la méthode annotée avant chaque test unitaire
     * Ici avant chaque test on initialise la machine à café
     */
    @BeforeEach
    public void beforeTest(){
        coffeeMachineUnderTest = new CoffeeMachine(
                0,10,
                0,10,  700);
    }

    /**
     * On vient tester si la machine ne se met pas en défaut
     */
    @Test
    public void testMachineFailureTrue(){
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir contrôler la valeur retournée
        //ici on veut qu'elle retourne 1.0
        //when : permet de définir quand sur quelle méthode établir le stub
        //thenReturn : va permettre de contrôler la valeur retournée par le stub
        Mockito.when(randomMock.nextGaussian()).thenReturn(1.0);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        //On vérifie que le booleen outOfOrder est bien à faux avant d'appeler la méthode
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));

        //on appelle la méthode qui met la machine en défaut
        //On a mocké l'objet random donc la valeur retournée par nextGaussian() sera 1
        //La machine doit donc se mettre en défaut
        coffeeMachineUnderTest.coffeeMachineFailure();

        Assertions.assertTrue(coffeeMachineUnderTest.isOutOfOrder());
        assertThat(true, is(coffeeMachineUnderTest.isOutOfOrder()));
    }

    /**
     * On vient tester si la machine se met en défaut
     */
    @Test
    public void testMachineFailureFalse(){
        //On créé un mock de l'objet random
        Random randomMock = Mockito.mock(Random.class, Mockito.withSettings().withoutAnnotations());
        //On vient ensuite stubber la méthode nextGaussian pour pouvoir contrôler la valeur retournée
        //ici on veut qu'elle retourne 0.6
        //when : permet de définir quand sur quelle méthode établir le stub
        //thenReturn : va permettre de contrôler la valeur retournée par le stub
        Mockito.when(randomMock.nextGaussian()).thenReturn(0.6);
        //On injecte ensuite le mock créé dans la machine à café
        coffeeMachineUnderTest.setRandomGenerator(randomMock);

        //On vérifie que le booleen outOfOrder est bien à faux avant d'appeler la méthode
        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));

        //on appelle la méthode qui met la machine en défaut
        //On a mocker l'objet random donc la valeur retournée par nextGaussian() sera 0.6
        //La machine doit donc NE PAS se mettre en défaut
        coffeeMachineUnderTest.coffeeMachineFailure();

        Assertions.assertFalse(coffeeMachineUnderTest.isOutOfOrder());
        //Ou avec Hamcrest
        assertThat(false, is(coffeeMachineUnderTest.isOutOfOrder()));
    }

    /**
     * On test que la machine se branche correctement au réseau électrique
     */
    @Test
    public void testPlugMachine(){
        Assertions.assertFalse(coffeeMachineUnderTest.isPlugged());

        coffeeMachineUnderTest.plugToElectricalPlug();

        Assertions.assertTrue(coffeeMachineUnderTest.isPlugged());
    }

    /**
     * On test qu'une exception est bien levée lorsque que le cup passé en paramètre retourne qu'il n'est pas vide
     * Tout comme le test sur la mise en défaut afin d'avoir un comportement isolé et indépendant de la machine
     * on vient ici mocker un objet Cup afin d'en maitriser complétement son comportement
     * On ne compte pas sur "le bon fonctionnement de la méthode"
     */
    @Test
    public void testMakeACoffeeCupNotEmptyException(){
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(false);

        coffeeMachineUnderTest.plugToElectricalPlug();

        //on reset la machine a cafe pour eviter une panne
        coffeeMachineUnderTest.reset();

        //assertThrows( [Exception class expected], [lambda expression with the method that throws an exception], [exception message expected])
        //AssertThrows va permettre de venir tester la levée d'une exception, ici lorsque que le contenant passé en
        //paramètre n'est pas vide
        //On teste à la fois le type d'exception levée mais aussi le message de l'exception
        Assertions.assertThrows(CupNotEmptyException.class, ()->{
            coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.MOKA);
        });
    }

    // Test machine out of order
    @Test
    public void testMakeACoffeeOutOfOrderException() throws Exception{

        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);
        Mockito.when(mockCup.getCapacity()).thenReturn(0.15); // Capacité de la tasse en millilitres

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On ajoute de l'eau
        coffeeMachineUnderTest.addWaterInTank(10);

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        coffeeMachineUnderTest.setOutOfOrder(true);

        // On appelle la méthode makeACoffee
        CoffeeContainer container = coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.ROBUSTA);

        // on verifie que le contenaire est null
        Assertions.assertNull(container);

    }

    // Test de la branche où coffeeType n'est pas égal au type de café dans le réservoir
    @Test
    public void testMakeACoffeeDifferentCoffeeTypeException() {
        Mug mockMug = Mockito.mock(Mug.class);
        Mockito.when(mockMug.isEmpty()).thenReturn(true);

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On vérifie que l'exception CoffeeTypeCupDifferentOfCoffeeTypeTankException est levée
        Assertions.assertThrows(CoffeeTypeCupDifferentOfCoffeeTypeTankException.class, () -> {
            coffeeMachineUnderTest.makeACoffee(mockMug, CoffeeType.ARABICA);
        });
    }

    // Test de la branche où coffeeType contient "_CREMA"
    @Test
    public void testMakeACoffeeCremaException() {
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ARABICA_CREMA);

        // On vérifie que l'exception CannotMakeCremaWithSimpleCoffeeMachine est levée
        Assertions.assertThrows(CannotMakeCremaWithSimpleCoffeeMachine.class, () -> {
            coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.ARABICA_CREMA);
        });
    }


    // Test de la méthode makeACoffee pour la préparation d'un café dans une tasse (Cup)
    @Test
    public void testMakeACoffeeInCup() throws Exception {

        // On crée un mock de Cup
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);
        Mockito.when(mockCup.getCapacity()).thenReturn(0.15); // Capacité de la tasse en millilitres

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On ajoute de l'eau
        coffeeMachineUnderTest.addWaterInTank(10);

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        // On appelle la méthode makeACoffee
        CoffeeContainer container = coffeeMachineUnderTest.makeACoffee(mockCup, CoffeeType.ROBUSTA);

        // On verifie que le contenaire de cafe est vide
        Assertions.assertNotNull(container);
    
        // On vérifie que CoffeeCup est instancié correctement avec la tasse, le type de café et qu'il est marqué comme vide
        Assertions.assertTrue(container instanceof CoffeeCup);

        //On verifie que le type de café est le bon
        Assertions.assertEquals(container.getCoffeeType(), CoffeeType.ROBUSTA);
        
    }

    // Test de la méthode makeACoffee pour la préparation d'un café dans une grande tasse (Mug)
    @Test
    public void testMakeACoffeeInMug() throws Exception {
        // On crée un mock de Mug
        Mug mockMug = Mockito.mock(Mug.class);
        Mockito.when(mockMug.isEmpty()).thenReturn(true);
        Mockito.when(mockMug.getCapacity()).thenReturn(0.35); // Capacité du mug en millilitres

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On ajoute de l'eau
        coffeeMachineUnderTest.addWaterInTank(10);

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        // On appelle la méthode makeACoffee
        CoffeeContainer container = coffeeMachineUnderTest.makeACoffee(mockMug, CoffeeType.ROBUSTA);

        // On verifie que le contenaire de cafe est vide
        Assertions.assertNotNull(container);
    
        // On vérifie que CoffeeCup est instancié correctement avec la tasse, le type de café et qu'il est marqué comme vide
        Assertions.assertTrue(container instanceof CoffeeMug);

        //On verifie que le type de café est le bon
        Assertions.assertEquals(container.getCoffeeType(), CoffeeType.ROBUSTA);
    }

    @Test
    public void TestMakeCoffeeAndMachineUnplugged(){
        // On crée un mock de Mug
        Mug mockMug = Mockito.mock(Mug.class);
        Mockito.when(mockMug.isEmpty()).thenReturn(true);
        Mockito.when(mockMug.getCapacity()).thenReturn(0.35); // Capacité du mug en millilitres

        // On ajoute de l'eau
        coffeeMachineUnderTest.addWaterInTank(10);

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        // On vérifie que l'exception MachineNotPluggedException est levée
        Assertions.assertThrows(MachineNotPluggedException.class, () -> {
            coffeeMachineUnderTest.makeACoffee(mockMug, CoffeeType.ARABICA_CREMA);
        });

    }

    @Test
    public void TestMakeCoffeeAndLackOfWater(){
        // On crée un mock de Mug
        Mug mockMug = Mockito.mock(Mug.class);
        Mockito.when(mockMug.isEmpty()).thenReturn(true);
        Mockito.when(mockMug.getCapacity()).thenReturn(0.35); // Capacité du mug en millilitres

        
        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        // On vérifie que l'exception MachineNotPluggedException est levée
        Assertions.assertThrows(LackOfWaterInTankException.class, () -> {
            coffeeMachineUnderTest.makeACoffee(mockMug, CoffeeType.ARABICA_CREMA);
        });

    }

    @Test
    public void TestToString() throws Exception{
        // On crée un mock de Cup
        Cup mockCup = Mockito.mock(Cup.class);
        Mockito.when(mockCup.isEmpty()).thenReturn(true);
        Mockito.when(mockCup.getCapacity()).thenReturn(0.15); // Capacité de la tasse en millilitres

        // On branche la machine à la prise électrique
        coffeeMachineUnderTest.plugToElectricalPlug();

        // On ajoute de l'eau
        coffeeMachineUnderTest.addWaterInTank(10);

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        String result = coffeeMachineUnderTest.toString();

        String expected = "Your coffee machine has : \n" +
        "- water tank : " + coffeeMachineUnderTest.getWaterTank().toString() + "\n" +
        "- water pump : " + coffeeMachineUnderTest.getWaterPump().toString() + "\n" +
        "- electrical resistance : " + coffeeMachineUnderTest.getElectricalResistance() + "\n" +
        "- is plugged : " + coffeeMachineUnderTest.isPlugged() + "\n"+
        "and made " + coffeeMachineUnderTest.getNbCoffeeMade() + " coffees";

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void TestChangeNbCoffeeMade(){

        coffeeMachineUnderTest.setNbCoffeeMade(12);

        Assertions.assertEquals(coffeeMachineUnderTest.getNbCoffeeMade(), 12);
    }

    @Test 
    public void TestAddCoffeeBeanTank(){

        // On ajoute le bon type de cafe
        coffeeMachineUnderTest.beanTank.setBeanCoffeeType(CoffeeType.ROBUSTA);

        //  On ajoute plus de cafe du meme type

        coffeeMachineUnderTest.beanTank.increaseCoffeeVolumeInTank(10, CoffeeType.ROBUSTA);

        Assertions.assertEquals(coffeeMachineUnderTest.getBeanTank().getActualVolume(), 10);
        
    }

    @AfterEach
    public void afterTest(){

    }
}