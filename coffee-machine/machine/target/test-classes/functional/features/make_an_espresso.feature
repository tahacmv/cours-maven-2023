Feature: Make a espresso with a complete espresso machine
  A user want a espresso

  Scenario: A user plug the espresso machine and make a coffee Arabica
    Given a espresso machine with 0.10 l of min capacity, 3.0 l of max capacity, 600.0 l per h of water flow for the pump
    And a espresso "mug" with a capacity of 0.25
    When I plug the espresso machine to electricity
    And I add 1 liter of water in the espresso machine water tank
    And I add 0.5 liter of "ARABICA" in the espresso machine bean tank
    And I made a coffee "ARABICA" in the espresso machine
    Then the espresso machine return a espresso mug not empty
    And a espresso volume equals to 0.25
    And a espresso "mug" containing a espresso type "ARABICA"


  Scenario: A user plug the espresso machine and make a coffee
    Given a espresso machine with 0.10 l of min capacity, 3.0 l of max capacity, 600.0 l per h of water flow for the pump
    And a espresso "cup" with a capacity of 0.15
    When I plug the espresso machine to electricity
    And I add 1 liter of water in the espresso machine water tank
    And I add 0.5 liter of "ROBUSTA" in the espresso machine bean tank
    And I made a coffee "ROBUSTA" in the espresso machine
    Then the espresso machine return a espresso mug not empty
    And a espresso volume equals to 0.15
    And a espresso "cup" containing a espresso type "ROBUSTA"

  Scenario: A user plugs the espresso machine and fills the water tank
    Given a espresso machine with 0.10 l of min capacity, 3.0 l of max capacity, 600.0 l per h of water flow for the pump
    When I plug the espresso machine to electricity
    And I add 1 liter of water in the espresso machine water tank
    Then the espresso machine is plugged
    And the espresso machine water tank contains 1.0 l of water

  Scenario: A user plug the espresso machine and make a espresso
    Given a espresso machine with 0.10 l of min capacity, 3.0 l of max capacity, 600.0 l per h of water flow for the pump
    And a espresso "cup" with a capacity of 0.15
    When I plug the espresso machine to electricity
    And I add 1 liter of water in the espresso machine water tank
    And I add 0.5 liter of "ARABICA_CREMA" in the espresso machine bean tank
    And I made a coffee "ARABICA_CREMA" in the espresso machine
    Then the espresso machine return a espresso mug not empty
    And a espresso volume equals to 0.15
    And a espresso "cup" containing a espresso type "ARABICA_CREMA"

