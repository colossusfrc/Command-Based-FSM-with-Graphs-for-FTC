# Command-Based FTC com comandos interrelacionados por grafos
 ##O que você precisa fazer:
 ###1. Crie seus subsistemas.
    Para cada subsistema, siga os passos abaixo:
    1. herde StatePortableSubsystem
    2. Associe a superestrutura do construtor
    3. implemente os métodos abstratos finalAction e actuateSubsystem()
 ###2. Crie seus estados 
     (este possui alguns como exemplo, mas pode mudá-los)
 ###3. No pacote commands-intercommands,
     crie uma classe para cada relação entre os estados que você fez,
    de modo que se receba um StatePortableSubsystem no seu construtor.
 ###4. No enum states,
     no bloco estático, relacione um estado ao outro com o acesso
    intermediateCommands.put seguido de seu estado alvo e de sua classe referenciada.
    Exemplo: Temos o estado S1 e S2, bem como a classe S1ToS2.java. Nesse bloco, faríamos:
    ```Java
    S1.intermediateCommands.put(S2, S1ToS2.class);
    ```
 ###5. Caso queira alguma análise mais complexa,
     coloque-a no método contracts, dentro da classe StateMachine.java
 ###6. Relacione os comandos principais
     aos botões na classe RobotContainer.java, seguindo a seguinte forma:
    ```Java
    new Trigger(()->[botao]).[acao](
      IntermediateCommandFactory
          .intermediateSelector(state, subsystem);
    )
    ```
Por enquanto, estamos suportando apenas 1 subsistema. Mas, em breve, faremos o teste para n subsistemas.
Só precisamos montar um protótipo que suporte essas características no laboratório.
Existe uma superclasse chamada StatePortableMachine.java, que herda SubsystemBase e possui alguns atributos e métodos.

```Java
public class StatePortableMachine{
//Atributos
    StateMachine statemachine;
    /*O statemachine é outra classe, que é um wrapper de states.
    Essa máquina de estados possui um método público setStates(States state), que 
    se responsabilizam por requerir um novo estado a partir de um regimento de contratos (privado)
    */
    LinearOpMode linearOpMode;
    //O linear opmode só serve para publicar as telemetrias.
//Métodos
    public void setState(States state);
    //Responsável por atuar um subssitema
    public states getStates();
    //retorna um estado de um subsistema
    public abstract void actuateSubsystem(States state);
    //método reponsável por transmutar o estado em uma instrução do subsistema
    public void actuateSubsystem(double value){}
    //método sobrecarregado, que recebe o valor diretamente.
}
```
Além dessa classe, existe o enum States.java
```Java
public enum States{
  //enum que possui alguns atributos e métodos (o que nos interessa são somente os métodos).
  public Optional<Command> intermediateCommand(States state, StatePortableSubsystem subsystem){}
  //A partir do estado alvo, constrói um comando que relaciona o anterior ao subsequente.
  //Caso contrário, retorna um elemento vazio.
  public Double getRelation(StatePortableSubsystem subsystem){}
  //retorna a relação entre o estado e o valor associado ao subssitema do parâmetro
  //existe um método de relação para cada estado, que recebe um subssitema. Esse método é usado
  //uma única vez, e associa o subsistema ao estado com um valor.
}
```
Além disso, existe uma classe FactoryIntermediateCommand.java, para simplificar o enum e torar a produção
de comandos mais acessível:
```Java
public class IntermediateCommandFactory{
  private static Command intermediateCommandSelector(States state, StatePortableSubsystem subsystem){}
  //Retorna um comando sequencial, que pode ter duas formas.
  //caso exista relação entre o estado atual e o hipotético (advindo do parâmetro), retorna um 
  //comando sequencial que realiza o comando intermediário e, posteriormente, o comando associado ao estado.
  //caso não exista, simplesmente realiza o comando associado.
  public static Command intermediateSelector(States state, StatePortableSubsystem subsystem){}
  //retorna um novo selectCommand, com uma implementação única para esse projeto, que possui como supplier esse
  //seletor de comandos (averiguar a necessidade disso).
}
```
Temos essa implementação do SelectCommand.java, que possui a seguinte estrutura:
```Java
public class SelectCommand{
  private Supplier<Command> command;
  private Command selectedCommand;
  protected void initialize(){}
  //inicializa o comando selecionado no instante da leitura
  protected void execute(){}
  //executa o comando selecionado no instante da leitura
  protected void end(boolean interrupted){}
  //fianliza o comando selecionado no instante da leitura
  protected boolean isFinished(){}
  //condição de parada do comando selecionado no instante da leitura
}
```
E, por fim, temos o pacote intercommands, que declara classes de interrelações (assim como qualquer outro comando).
Sua estrutura geral pode ser abstraída por uma superclasse InterCommands.java:
```Java
public class InterCommands extends Command{
  protected final StatePortableSubsystem statePortableSubsystem;
  public InterCommands(StatePortableSubsystem statePortableSubsystem){}
  //inicializamos o atributo, e o aproveitamos nas outras classes intermediárias.
}
```
