# <span style="color:#4CAF50;">**Projeto de PI do Pedro e do Guilherme**</span>

| **Número** | **Nome do Membro**       |
|------------|--------------------------|
| `111493`   | **Pedro Amaral**         |
| `111138`   | **Guilherme Teixeira**   |



# <span style="color:#4CAF50;">**Sobre o Projeto**</span>
> O projeto é um jogo do estilo roguelike em que cada "sala" tem itens e inimigos aleatórios. Usando uma temática em que o personagem principal é um slime. Concluindo a sala o jogador terá a opção de escolher entre 2 ou 3 salas para ser a sua proxima e o tipo de sala tambem é aleatório. Cada sessão de jogo o jogo será diferente. Essas sessões de jogo são o que chamamos de run. Uma run começa quando se inicia o jogo, ao morrer-se na run esta acaba e começa-se uma nova. Nome do jogo **TBD**.

Conceito: És um Slime que tem de limpar salas numa masmorra (Dungeon). Ao acabar uma sala, aparecem 2 ou 3 portas para a próxima. O objetivo é chegar o mais longe possível ou finalizar o jogo matando todos os bosses do jogo (número ainda por definir).

**Variáveis Aleatórias:**
- V.A. Discreta (Poisson): Número de Inimigos por Sala.
- V.A. Discreta (Binomial): Probabilidade das próximas salas serem "Tesouro" vs "Combate".
- V.A. Discreta (Uniforme): Ordem aleatória dos Bosses (Permutação).
- V.A. Contínua (Normal): Dano de Ataque do Slime.
- V.A. Contínua (Exponencial): Movimentação Aleatória dos Inimigos.
- V.A. Contínua (Uniforme): Posição $(X, Y)$ dos itens no chão.
