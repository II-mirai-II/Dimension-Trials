# SUMÁRIO DO PROJETO

Este documento mapeia e explica a estrutura do mod Dimension Trials, servindo como referência rápida para compreender a organização e funcionalidades do projeto.

## Índice
- [Visão Geral](#visão-geral)
- [Estrutura de Pacotes](#estrutura-de-pacotes)
- [Utilities](#utilities)
  - [I18nHelper](#i18nhelper)
  - [MobUtils](#mobutils)
  - [NotificationHelper](#notificationhelper)
  - [PartyUtils](#partyutils)
- [Classe Principal](#classe-principal)
- [Sistemas Principais](#sistema-de-progressão)
  - [Sistema de Progressão](#sistema-de-progressão)
  - [Sistema de Parties](#sistema-de-parties)
  - [Sistema de Networking](#sistema-de-networking)
  - [Sistema de Configuração](#sistema-de-configuração)
  - [Sistema de Requisitos Personalizados](#sistema-de-requisitos-personalizados)
- [Client](#client)
  - [Dados de Progressão Client](#dados-de-progressão-client)
  - [GUI](#gui)
    - [Screens](#screens)
    - [Sections](#sections)
    - [Implementações de Seções Específicas](#implementações-de-seções-específicas)
- [Data](#data)
  - [Sistema de Parties](#sistema-de-parties-data)
  - [Coordenação de Progressão](#coordenação-de-progressão)
  - [ProgressionManager](#progressionmanager)
- [Event](#event)
  - [Sistema de Manipulação de Eventos](#sistema-de-manipulação-de-eventos)
  - [ModEventHandlers](#modeventhandlers)
  - [MobMultiplierHandler](#mobmultiplierhandler)
  - [XpMultiplierHandler](#xpmultiplierhandler)
- [Integration](#integration)
  - [Sistema de Integração com Mods Externos](#sistema-de-integração-com-mods-externos)
  - [ExternalModIntegration](#externalmodintegration)
- [Network](#network)
  - [Sistema de Comunicação Cliente-Servidor](#sistema-de-comunicação-cliente-servidor)
  - [BatchSyncProcessor](#batchsyncprocessor)
  - [DeltaUpdateSystem](#deltaupdatesystem)
  - [ModNetworking](#modnetworking)
  - [UpdatePartyToClientPayload](#updatepartytoclientpayload)
  - [UpdateProgressionToClientPayload](#updateprogressiontoclientpayload)
- [Sync](#sync)
  - [Sistema de Sincronização Centralizada](#sistema-de-sincronização-centralizada)
  - [SyncManager](#syncmanager)
- [System](#system)
  - [Sistema de Backup e Validação](#sistema-de-backup-e-validação)
  - [BackupManager](#backupmanager)
  - [BossKillValidator](#bosskillvalidator)
  - [CustomPhaseSystem](#customphasesystem)
  - [DataValidator](#datavalidator)
  - [ProgressTransferService](#progresstransferservice)
  - [StateRecoveryManager](#staterecoverymanager)
- [Utilidades e Constantes](#utilidades-e-constantes)
- [Integração com Mods Externos](#integração-com-mods-externos)
- [Comandos](#comandos)
- [Conclusão](#conclusão)
- [Conclusão](#conclusão)

## Visão Geral

Dimension Trials é um mod para Minecraft que implementa um sistema de progressão em fases e desafios para jogadores. O mod usa o NeoForge como framework base e implementa vários sistemas interconectados:

1. **Sistema de Progressão por Fases**: Jogadores avançam através de diferentes fases completando objetivos específicos
2. **Sistema de Parties Colaborativas**: Permite que jogadores formem grupos para progredir coletivamente
3. **Interface de HUD Interativa**: Exibe o progresso do jogador de forma intuitiva e organizada
4. **Sistema de Configuração Abrangente**: Permite personalizar todos os aspectos do mod
5. **Integração com Outros Mods**: Suporte para extensão e integração com outros mods

## Estrutura de Pacotes

O projeto é organizado principalmente no pacote base `net.mirai.dimtr`, com as seguintes subpastas principais:

- `client`: Componentes exclusivos do lado do cliente
- `command`: Comandos administrativos e de party
- `config`: Sistema de configuração e requisitos personalizados
- `data`: Sistema de gerenciamento de dados (progressão, parties)
- `integration`: Integração com mods externos
- `network`: Sistema de comunicação cliente-servidor
- `util`: Classes utilitárias e constantes

## Utilities

As classes utilitárias fornecem funcionalidades centralizadas e reutilizáveis em todo o projeto, facilitando manutenção e consistência.

### I18nHelper

Classe utilitária para internacionalização (i18n) de mensagens de texto.

**Localização**: `net.mirai.dimtr.util.I18nHelper`

**Responsabilidades**:
- Centralizar a criação de componentes traduzíveis
- Fornecer suporte a fallback para traduções ausentes
- Facilitar envio de mensagens traduzidas para jogadores

**Métodos Principais**:
- `translatable(String key, Object... args)`: Cria componente traduzível básico
- `translatableWithFallback(String key, String fallback, Object... args)`: Cria componente com texto de fallback
- `sendMessage(ServerPlayer player, String key, Object... args)`: Envia mensagem do sistema traduzida
- `sendFailure(ServerPlayer player, String key, Object... args)`: Envia mensagem de falha traduzida

**Integração com o Sistema**:
- Usado extensivamente por comandos para feedback consistente
- Integra com `Constants` para chaves de tradução padronizadas
- Suporte completo ao sistema de localização do Minecraft

### MobUtils

Classe utilitária para classificação e categorização de mobs hostis.

**Localização**: `net.mirai.dimtr.util.MobUtils`

**Responsabilidades**:
- Identificar mobs hostis para aplicação de multiplicadores
- Manter listas atualizadas de mobs relevantes para o sistema
- Fornecer diferentes categorias de mobs para diferentes mecânicas

**Métodos Principais**:
- `isHostileMob(LivingEntity entity)`: Verifica se é mob hostil (versão completa)
- `isBasicHostileMob(LivingEntity entity)`: Verifica mobs básicos (para sistema XP)

**Categorias de Mobs Suportadas**:
- **Mobs Básicos**: Zombie, Skeleton, Spider, Creeper, Witch, etc.
- **Mobs Avançados**: Elder Guardian, Blaze, Wither Skeleton, PiglinBrute
- **Mobs Boss**: Wither, Warden
- **Mobs Novos**: Bogged, Breeze (1.21+)

**Integração com o Sistema**:
- Usado por `MobMultiplierHandler` para aplicar multiplicadores
- Usado por `XpMultiplierHandler` para XP aumentado
- Mantém compatibilidade com diferentes versões do Minecraft

### NotificationHelper

Sistema robusto para envio de notificações visuais e sonoras aos jogadores.

**Localização**: `net.mirai.dimtr.util.NotificationHelper`

**Responsabilidades**:
- Enviar notificações categorizadas com estilo visual consistente
- Tocar sons apropriados para cada tipo de notificação
- Fornecer feedback rico para conquistas e progresso
- Coordenar efeitos de celebração especiais

**Tipos de Notificação (`NotificationType`)**:
- `SUCCESS`: Verde com ✅, som de XP
- `INFO`: Azul com ℹ️, som de sino
- `WARNING`: Amarelo com ⚠️, som de alerta
- `ERROR`: Vermelho com ❌, som de erro
- `ACHIEVEMENT`: Dourado com 🏆, som de conquista
- `PARTY`: Roxo claro com 👥, som de party

**Métodos Especializados**:
- `sendProgressUpdate()`: Notificações de progresso de objetivos
- `sendPhaseCompletion()`: Celebração de conclusão de fases
- `sendPartyJoinNotification()`: Notificações de entrada em party
- `sendMultiplierGained()`: Notificações de multiplicadores obtidos
- `launchCelebrationFireworks()`: Fogos de artifício para celebrações

**Integração com o Sistema**:
- Usado por todos os sistemas para feedback consistente
- Integra com `Constants` para mensagens padronizadas
- Coordena com `PartyUtils` para notificações de grupo

### PartyUtils

Classe utilitária centralizada para operações relacionadas ao sistema de parties.

**Localização**: `net.mirai.dimtr.util.PartyUtils`

**Responsabilidades**:
- Calcular multiplicadores de requisitos baseados no tamanho da party
- Gerenciar sincronização de progresso entre membros
- Facilitar notificações para grupos
- Validar estado de parties

**Métodos de Cálculo**:
- `calculateRequirementMultiplier(int memberCount)`: Calcula multiplicador (1.0 + 0.5 por membro adicional, máx 3.0x)
- `isPlayerInValidParty(UUID playerId, ServerLevel serverLevel)`: Verifica se jogador está em party válida

**Métodos de Gerenciamento**:
- `getOnlinePartyMembers(PartyData party, ServerLevel serverLevel)`: Obtém membros online
- `notifyPartyMembers()`: Envia notificações para toda a party
- `checkAndNotifyPartyObjective()`: Verifica e notifica objetivos concluídos

**Sincronização de Progresso**:
- `syncPlayerWithParty(UUID playerId, ServerLevel serverLevel)`: Sincroniza progresso bidirecional
- Mantém consistência entre progresso individual e de grupo
- Atualiza conquistas compartilhadas (Elder Guardian, etc.)

**Integração com o Sistema**:
- Usado por `ProgressionManager` para cálculos de requisitos
- Integra com `NotificationHelper` para feedback de grupo
- Coordena com `PartyManager` para dados de party

## Classe Principal

### DimTrMod

Classe principal e ponto de entrada do mod Dimension Trials.

**Localização**: `net.mirai.dimtr.DimTrMod`

**Responsabilidades**:
- Inicializar todos os sistemas do mod
- Registrar configurações cliente e servidor
- Configurar sistema de networking
- Coordenar inicialização de recursos e integrações

**Constantes Principais**:
- `MODID`: Identificador único do mod ("dimtr")
- `LOGGER`: Logger dedicado para rastreamento e debug

**Processo de Inicialização**:
1. **Registro de Configurações**:
   - Configuração de servidor: `DimTrConfig.SERVER_SPEC`
   - Configuração de cliente: `DimTrConfig.CLIENT_SPEC`
   - Arquivos salvos como `dimtr-server.toml` e `dimtr-client.toml`

2. **Sistema de Networking**:
   - Registra payloads através do `ModNetworking.registerPayloads()`
   - Configurado no event bus para inicialização automática

3. **Sistemas Avançados (FMLCommonSetupEvent)**:
   - Carregamento de requisitos customizados via `CustomRequirements.loadCustomRequirements()`
   - Inicialização de integração com mods externos via `ExternalModIntegration.initialize()`
   - Execução em `enqueueWork()` para thread-safety

**Logging de Funcionalidades**:
- Sistema de progressão por fases
- Sistema de parties colaborativas
- Interface HUD modular
- Comandos administrativos e de party
- Rastreamento individual de progresso
- Multiplicadores por proximidade
- Sistema de requisitos personalizados

**Integração com Outros Sistemas**:
- Coordena com `Constants` para mensagens de log padronizadas
- Integra com sistemas de configuração para customização
- Gerencia ciclo de vida de todos os componentes do mod

## Client

### Dados de Progressão Client

#### `ClientProgressionData`

Classe singleton que armazena e gerencia os dados de progressão do jogador no cliente.

**Localização**: `net.mirai.dimtr.client.ClientProgressionData`

**Responsabilidades**:
- Armazenar o estado atual da progressão do jogador
- Receber e processar atualizações de dados do servidor
- Fornecer métodos para verificar a conclusão de fases e objetivos

**Campos Principais**:
- **Objetivos Principais**:
  - `elderGuardianKilled`: Indica se o Elder Guardian foi derrotado
  - `raidWon`: Indica se uma raid foi concluída com sucesso
  - `trialVaultAdvancementEarned`: Conquista relacionada ao Trial Vault
  - `voluntaireExileAdvancementEarned`: Conquista Voluntary Exile
  - `phase1Completed`: Estado de conclusão da Fase 1
  - `witherKilled`: Indica se o Wither foi derrotado
  - `wardenKilled`: Indica se o Warden foi derrotado
  - `phase2Completed`: Estado de conclusão da Fase 2

- **Contadores de Mobs (Fase 1)**:
  - Rastreamento de mortes de mobs do Overworld (ex: zombie, skeleton, creeper, etc.)
  - Valores atuais e requisitos configuráveis

- **Contadores de Mobs (Fase 2)**:
  - Rastreamento de mortes de mobs do Nether (ex: blaze, wither skeleton, etc.)
  - Valores atuais e requisitos configuráveis

- **Fases Personalizadas**:
  - `customPhaseCompletion`: Mapeia fases personalizadas e seu estado de conclusão
  - `customMobKills`: Rastreia mortes de mobs para fases personalizadas
  - `customObjectiveCompletion`: Rastreia conclusão de objetivos personalizados

**Métodos Importantes**:
- `updateData(UpdateProgressionToClientPayload)`: Atualiza os dados de progressão com informações do servidor
- `isPhase1EffectivelyComplete()`: Verifica se a Fase 1 está efetivamente completa
- Vários getters para acessar campos privados

### GUI

#### Screens

##### `ProgressionHUDScreen`
**Arquivo:** `net.mirai.dimtr.client.gui.screens.ProgressionHUDScreen`

Interface gráfica principal para visualização do progresso do jogador, implementada com design modular e sistema de scroll vertical.

**Funcionalidades principais:**
- Sistema de navegação entre uma visão geral (sumário) e seções específicas de conteúdo
- Layout responsivo que se adapta às dimensões da tela do jogador
- Sistema de scroll vertical para navegação em listas extensas de seções
- Paginação para visualização de conteúdo extenso dentro de seções específicas
- Integração com o sistema de Parties
- Feedback visual e sonoro para interações do usuário

**Estados de navegação:**
- `SUMMARY`: Exibe visão geral com todas as seções disponíveis
- `SECTION`: Exibe conteúdo detalhado de uma seção específica

**Sistema de renderização:**
- Renderização dinâmica baseada em dimensões calculadas proporcionalmente à tela
- Sistema de clipping para controle de visibilidade durante o scroll
- Renderização de scrollbar interativa com feedback visual
- Separação de conteúdo em colunas para melhor legibilidade

**Interatividade:**
- Controles de teclado para navegação (setas, ESC, teclas Q/E para paginação)
- Controles de mouse para seleção de seções e scroll
- Feedback sonoro para todas as interações

**Integração:**
- Utiliza o `SectionManager` para gerenciar módulos de conteúdo
- Consome dados do `ClientProgressionData` para exibir progresso atual
- Implementa interface modular via `HUDSection` para expansibilidade

**Evolução:**
- Sistema inicialmente baseado em tabs evoluiu para um sistema de scroll vertical
- Suporte a múltiplas fases de progressão incluindo fases customizadas
- Adaptação para diferentes resoluções de tela

**Detalhes técnicos:**
- Estende a classe `Screen` do Minecraft
- Implementa cálculos dinâmicos para posicionamento de elementos
- Utiliza sistema de scissor/clipping para visualização parcial durante scroll

#### Sections

##### `HUDSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.HUDSection`

Interface base que define o contrato para todas as seções exibidas no HUD de progressão, garantindo modularidade e extensibilidade.

**Funcionalidades principais:**
- Define métodos para obtenção de identificação, título e conteúdo da seção
- Estabelece o padrão para verificação de acessibilidade baseada no progresso do jogador
- Permite geração dinâmica de conteúdo contextual baseado no progresso atual

**Métodos principais:**
- `getType()`: Retorna o tipo único da seção via enum `SectionType`
- `getTitle()`: Retorna o componente de texto para o título da seção
- `getDescription()`: Retorna a descrição que será exibida no sumário
- `getIcon()`: Retorna o ícone (emoji) que representa visualmente a seção
- `isAccessible()`: Determina se a seção está disponível com base no progresso
- `generateContent()`: Gera dinamicamente o conteúdo textual da seção

**Enum SectionType:**
- Define tipos de seções suportadas com suas chaves de tradução e ícones
- Inclui seções para fases principais (1, 2, 3), objetivos específicos, grupos e fases customizadas
- Cada tipo armazena sua chave de tradução e ícone para consistência visual

**Integração:**
- Utilizada pelo `SectionManager` para registrar e gerenciar seções disponíveis
- Implementada por todas as seções específicas do HUD
- Consumida pelo `ProgressionHUDScreen` para renderização dinâmica

##### `Phase2GoalsSection`

**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase2GoalsSection`

Implementação da interface `HUDSection` focada nos objetivos de eliminação de mobs da Fase 2 (Nether).

**Funcionalidades principais:**
- Exibe contadores detalhados de mobs eliminados na Fase 2
- Organiza os mobs em três categorias: Nether, Overworld repetidos e mobs-objetivo
- Mostra progresso atual versus requisitos aumentados para esta fase
- Fornece resumo estatístico separado para progresso no Nether e no Overworld

**Categorias de mobs:**
- **Mobs do Nether**: Blaze, Wither Skeleton, Piglin Brute, Hoglin, Zoglin, Ghast, Piglin
- **Mobs do Overworld (requisitos aumentados)**: Zombie, Skeleton, Creeper, Spider, Enderman, Witch, Pillager
- **Mobs-Objetivo resetados**: Ravager, Evoker (novos requisitos para a Fase 2)

**Estatísticas e resumo:**
- Progresso do Nether: contagem de tipos de mobs do Nether completos
- Progresso do Overworld: contagem de tipos de mobs do Overworld completos
- Formatação visual diferenciada para os dois tipos de progresso

**Comportamento contextual:**
- Verifica se a Fase 1 está efetivamente completa para permitir acesso
- Exibe mensagem informativa quando o acesso está bloqueado
- Verifica se o sistema de eliminação de mobs da Fase 2 está habilitado
- Exibe mensagens apropriadas quando sistemas estão desabilitados

**Tratamento de dados:**
- Acessa dados de eliminações via mapa centralizado de contadores
- Verifica requisitos específicos para a Fase 2 (geralmente mais elevados)
- Aplica lógica de comparação para determinar status de conclusão
- Organiza os contadores de forma clara e com categorização lógica

###### `Phase2MainSection`

Implementação da `HUDSection` dedicada à exibição da visão geral e objetivos principais da Fase 2 (Nether).

**Localização**: `net.mirai.dimtr.client.gui.sections.Phase2MainSection`

**Análise Linha a Linha**:

```java
package net.mirai.dimtr.client.gui.sections;
```
- Define o pacote onde a classe está localizada, agrupada com outras implementações de seções do HUD.

```java
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
```
- Importações revelam dependências essenciais:
  - `ClientProgressionData`: Dados de progressão do jogador
  - `Constants`: Chaves de tradução e constantes
  - `ChatFormatting`: Formatação visual (cores) do texto
  - `Component`: Sistema de texto localizado do Minecraft
  - `ArrayList`/`List`: Estruturas para construir o conteúdo da seção

```java
/**
 * Seção principal da Fase 2
 */
public class Phase2MainSection implements HUDSection {
```
- Comentário descreve o propósito da seção: apresentar a visão principal da Fase 2
- Implementa a interface `HUDSection` para integração no sistema modular de seções

```java
    @Override
    public SectionType getType() {
        return SectionType.PHASE2_MAIN;
    }
```
- Implementação do método `getType()` da interface
- Retorna o tipo enum específico para esta seção: `PHASE2_MAIN`
- Permite que o `SectionManager` identifique e registre esta seção corretamente

```java
    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }
```
- Cria um título composto por:
  - Um componente literal com o ícone da seção (emoji 🌌)
  - Um espaço
  - Um componente traduzível usando a chave do tipo de seção
- Permite localização do título em diferentes idiomas

```java
    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase2_main.desc");
    }
```
- Retorna a descrição localizada da seção
- Usa diretamente a chave de tradução ao invés de uma constante

```java
    @Override
    public String getIcon() {
        return getType().getIcon();
    }
```
- Implementação simples que delega para o ícone definido no enum `SectionType`
- Mantém consistência visual com a definição central de ícones

```java
    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.isServerEnablePhase2() && progress.isPhase1EffectivelyComplete();
    }
```
- Define a lógica de acessibilidade para esta seção com duas condições:
  - Verifica se a Fase 2 está habilitada na configuração do servidor
  - Verifica se a Fase 1 foi efetivamente concluída pelo jogador
- Não verifica rastreamento de mobs (diferente de `Phase2GoalsSection`)
- Mais acessível que a seção de objetivos, priorizando visibilidade da visão geral

```java
    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        // 🎯 NOVO: Ensure client-side external mod integration is initialized
        net.mirai.dimtr.integration.ExternalModIntegration.initializeClientSide();
```
- Início do método principal que gera todo o conteúdo da seção
- Comentário indica adição recente: inicialização da integração com mods externos
- Chamada para garantir que a integração com mods externos esteja inicializada
- Consistente com `Phase1MainSection`, demonstrando padrão uniforme

```java
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.translatable("gui.dimtr.complete.phase1.first")
                    .withStyle(ChatFormatting.RED));
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.phase2.locked.line1")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase2.locked.line2")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase2.locked.line3")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }
```
- Cria lista vazia para acumular componentes de texto
- Primeira verificação de segurança: fase anterior concluída
- Mensagem de erro em vermelho se a Fase 1 não estiver concluída
- Adiciona três linhas de explicação em cinza sobre o porquê a Fase 2 está bloqueada
- Mais informativo que `Phase2GoalsSection`, fornecendo contexto adicional
- Padrão de "falha rápida" retornando imediatamente com as mensagens

```java
        if (!progress.isServerEnablePhase2()) {
            content.add(Component.translatable("gui.dimtr.phase2.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }
```
- Segunda verificação de segurança: Fase 2 habilitada no servidor
- Mensagem específica em cinza se a fase estiver desabilitada
- Continua o padrão de "falha rápida"

```java
        if (progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.phase.complete")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }
```
- Verificação se a Fase 2 já está concluída
- Em caso positivo, adiciona mensagem destacada em verde e negrito
- Adiciona linha vazia para espaçamento visual
- Não retorna imediatamente, continua mostrando detalhes mesmo com fase concluída
- Consistente com a abordagem de `Phase1MainSection`

```java
        // Objetivos especiais
        content.add(Component.translatable(Constants.HUD_SECTION_SPECIAL_OBJECTIVES)
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
```
- Adiciona cabeçalho "Objetivos Especiais" em roxo claro e negrito
- Usa constante para a chave de tradução
- Diferente de `Phase1MainSection` que usa dourado, adaptado à temática do Nether

```java
        if (progress.isServerReqWither()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_WITHER_KILLED),
                    progress.isWitherKilled()));
        }
```
- Verificação condicional para objetivo do Wither
- Verifica primeiro se o objetivo está habilitado no servidor via `isServerReqWither()`
- Só adiciona a linha se o objetivo estiver ativo na configuração
- Chamada método auxiliar `createGoalLine` que formata a linha com ícone de status
- Passa:
  - Componente traduzível para o texto do objetivo
  - Status de conclusão via `isWitherKilled()`
- Padrão consistente com o objetivo do Elder Guardian em `Phase1MainSection`

```java
        if (progress.isServerReqWarden()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_WARDEN_KILLED),
                    progress.isWardenKilled()));
        }
```
- Verificação similar para o objetivo do Warden
- Segue o mesmo padrão do objetivo do Wither
- Demonstra consistência no tratamento dos objetivos principais

```java
        // 🎯 NOVO: Bosses de mods externos para Fase 2
        var externalBossesPhase2 = progress.getExternalBossesForPhase(2);
        if (!externalBossesPhase2.isEmpty()) {
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.external.bosses.phase2")
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

            for (var boss : externalBossesPhase2) {
                boolean killed = progress.isExternalBossKilled(boss.entityId);
                content.add(createGoalLine(
                        Component.literal(boss.displayName),
                        killed));
            }
        }
```
- Comentário indica funcionalidade recente: integração com bosses de mods externos
- Obtém lista de bosses externos definidos para a Fase 2
- Verifica se a lista não está vazia antes de adicionar seção
- Adiciona cabeçalho específico para bosses externos
- Itera sobre cada boss externo:
  - Verifica se o boss foi derrotado
  - Adiciona linha formatada usando o mesmo método auxiliar dos objetivos principais
  - Usa nome de exibição diretamente, sem tradução
- Segue o mesmo padrão de `Phase1MainSection`, demonstrando consistência arquitetural

```java
        content.add(Component.empty());

        // Status da fase
        if (progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.end.unlocked")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.translatable("gui.dimtr.complete.objectives")
                    .withStyle(ChatFormatting.YELLOW));
            content.add(Component.translatable("gui.dimtr.unlock.end")
                    .withStyle(ChatFormatting.YELLOW));
        }
```
- Linha vazia para separação visual
- Seção de status da fase que se adapta ao progresso atual:
  - Se fase completa: mensagem verde indicando que o End está desbloqueado
  - Se incompleta: duas mensagens amarelas orientando o jogador a completar objetivos para desbloquear o End
- Demonstra UI adaptativa com base no estado de progressão
- Padrão consistente com `Phase1MainSection`, apenas alterando a dimensão referenciada (End vs. Nether)

```java
        content.add(Component.empty());
        content.add(Component.translatable("gui.dimtr.unique.challenges")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        content.add(Component.translatable("gui.dimtr.challenge.wither")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.warden")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.nether")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.new.mobs")
                .withStyle(ChatFormatting.GRAY));
```
- Seção adicional não presente em `Phase1MainSection`
- Adiciona cabeçalho "Desafios Únicos" em dourado e negrito
- Lista quatro desafios específicos da Fase 2 em cinza:
  - Derrotar o Wither
  - Derrotar o Warden
  - Explorar o Nether
  - Enfrentar novos tipos de mobs
- Fornece contexto e orientação adicional sobre a temática da fase
- Demonstra personalização de conteúdo específico para cada fase

```java
        return content;
    }
```
- Retorna a lista completa de componentes gerados
- Todo o conteúdo é montado em memória e retornado para renderização

```java
    private Component createGoalLine(Component text, boolean completed) {
        ChatFormatting statusColor = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        String statusIcon = completed ? "✔" : "❌";

        return Component.literal(statusIcon + " ").withStyle(statusColor)
                .append(text.copy().withStyle(ChatFormatting.WHITE));
    }
```
- Método auxiliar que encapsula a formatação visual de uma linha de objetivo
- Parâmetros:
  - `text`: Componente com texto já traduzido para o objetivo
  - `completed`: Status de conclusão do objetivo
- Define cor e ícone baseados na conclusão:
  - Verde escuro e ✔ para concluído
  - Vermelho e ❌ para não concluído
- Constrói componente composto:
  - Ícone de status com cor apropriada
  - Texto do objetivo em branco (usando cópia do componente original)
- Idêntico ao método em `Phase1MainSection`, demonstrando consistência entre fases

**Responsabilidades Principais**:

1. **Apresentação de Objetivos Principais da Fase 2**
   - Exibe status dos objetivos especiais: Wither e Warden
   - Mostra apenas objetivos habilitados na configuração
   - Fornece feedback visual claro sobre conclusão (✔/❌)

2. **Integração com o End**
   - Mostra claramente se a fase está concluída
   - Informa sobre desbloqueio do End
   - Orienta o jogador sobre próximos passos

3. **Orientação Contextual**
   - Fornece mensagens detalhadas quando a fase está bloqueada
   - Apresenta lista de desafios específicos da Fase 2
   - Contextualiza a experiência do jogador no Nether

4. **Integração com Mods Externos**
   - Inicializa o sistema de integração com mods externos
   - Exibe bosses de mods externos relevantes para a Fase 2
   - Mantém experiência consistente independente da origem do conteúdo

**Técnicas de Design Implementadas**:

1. **Conteúdo Adaptativo**
   - Exibe diferentes mensagens baseadas no progresso atual
   - Fornece explicações detalhadas quando a fase está bloqueada
   - Mostra desafios específicos relevantes para o contexto do Nether

2. **Consistência Temática**
   - Uso de roxo claro para cabeçalhos (tema do Nether)
   - Referências específicas a desafios do Nether
   - Mensagens contextualizadas para a segunda fase da progressão

3. **Verificação de Pré-requisitos**
   - Verificação clara da conclusão da Fase 1
   - Mensagens informativas sobre o motivo do bloqueio
   - Redirecionamento apropriado para completar a fase anterior

4. **Reutilização de Padrões**
   - Mantém estrutura similar à `Phase1MainSection` para experiência consistente
   - Usa mesmo método auxiliar para formatação de objetivos
   - Aplica mesmos padrões de verificação e feedback visual

**Diferenças com `Phase1MainSection`**:

1. **Mensagens de Bloqueio Expandidas**
   - `Phase2MainSection`: Fornece três linhas de explicação quando bloqueada
   - `Phase1MainSection`: Mensagem simples de fase desabilitada

2. **Seção de Desafios Únicos**
   - `Phase2MainSection`: Inclui seção adicional de desafios específicos
   - `Phase1MainSection`: Não possui esta seção contextual

3. **Objetivos Adaptados**
   - `Phase2MainSection`: Foca em Wither e Warden
   - `Phase1MainSection`: Foca em Elder Guardian, Raid e conquistas específicas

4. **Cores Temáticas**
   - `Phase2MainSection`: Usa roxo claro para cabeçalhos de objetivos (temática do Nether)
   - `Phase1MainSection`: Usa dourado para cabeçalhos de objetivos (temática do Overworld)

Estas diferenças demonstram como o sistema mantém consistência estrutural enquanto adapta o conteúdo e a apresentação visual para refletir a identidade única de cada fase da progressão.

##### `PartiesSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.PartiesSection`

Implementação da interface `HUDSection` focada na exibição e gerenciamento de informações sobre grupos/parties de jogadores.

**Funcionalidades principais:**
- Exibe o status atual do jogador em relação a grupos (se está em um grupo ou não)
- Mostra detalhes do grupo atual (nome, tipo, membros, líder) quando aplicável
- Apresenta informações sobre progresso compartilhado entre membros do grupo
- Fornece instruções sobre comandos disponíveis para gerenciamento de grupos

**Comportamento contextual:**
- Quando em um grupo: exibe detalhes do grupo, lista de membros e progresso compartilhado
- Quando sem grupo: exibe comandos e instruções para criar ou entrar em grupos
- Indica o impacto do grupo no multiplicador de requisitos (aumento baseado no número de membros)
- Destaca o líder do grupo com um ícone de coroa (👑)

**Progresso compartilhado:**
- Exibe objetivos principais compartilhados (Elder Guardian, Raid, Wither, Warden)
- Mostra fases customizadas que foram compartilhadas entre os membros
- Utiliza formatação visual (cores e ícones) para indicar status de completude

**Benefícios de grupo:**
- Explica vantagens de jogar em grupo
- Detalha o funcionamento do compartilhamento de progresso
- Fornece informações sobre como o progresso é preservado ao sair de um grupo

**Integração:**
- Consome dados de `ClientPartyData` para obter informações atualizadas sobre grupos
- Utiliza constantes de `Constants` para exibição consistente de textos
- Registrada no `SectionManager` para exibição no HUD de progressão

##### `CustomPhasesSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.CustomPhasesSection`

Implementação da interface `HUDSection` que gerencia a exibição de fases e objetivos customizados, incluindo integração com mods externos.

**Funcionalidades principais:**
- Exibe fases customizadas configuradas via sistema de `CustomRequirements`
- Integra-se com mods externos populares como Mowzie's Mobs e L_Ender's Cataclysm
- Apresenta status de conclusão para cada fase customizada
- Fornece informações sobre bosses de mods externos organizados por fase de progressão

**Detecção de mods:**
- Verifica dinamicamente a presença de mods compatíveis via FML ModList
- Adapta o conteúdo exibido com base nos mods instalados
- Suporta fallback para configurações customizadas quando mods não estão presentes

**Organização de conteúdo:**
- Agrupa bosses de Mowzie's Mobs na Fase 1 (Overworld)
- Distribui bosses de L_Ender's Cataclysm entre fases 1, 2 (Nether) e 3 (End)
- Exibe fases totalmente customizadas com nome, descrição e status de conclusão
- Utiliza formatação visual (cores e ícones) para indicar status e organização

**Acessibilidade:**
- A seção só é acessível quando mods compatíveis estão instalados ou fases customizadas estão configuradas
- Fornece mensagem explicativa quando não há fases customizadas disponíveis
- Inclui referência à configuração para usuários interessados em criar fases personalizadas

**Integração:**
- Consome dados de `ClientProgressionData` para verificar status de conclusão
- Utiliza `CustomRequirements` para obter definições de fases customizadas
- Registrada no `SectionManager` para exibição no HUD de progresso

##### SectionManager
**Arquivo:** `net.mirai.dimtr.client.gui.sections.SectionManager`

Gerenciador centralizado responsável por registrar, organizar e fornecer acesso a todas as seções do HUD de progressão.

**Funcionalidades principais:**
- Mantém um registro centralizado de todas as seções do HUD via mapa estático
- Inicializa todas as seções no bloco estático durante o carregamento da classe
- Fornece métodos para acessar seções específicas ou listar todas as seções disponíveis
- Filtra seções com base em seu estado de acessibilidade para um determinado progresso

**Métodos essenciais:**
- `register()`: Registra uma seção no gerenciador, associando-a ao seu tipo único
- `getSection()`: Recupera uma seção específica por seu tipo enumerado
- `getAllSections()`: Retorna uma lista com todas as seções registradas
- `getAccessibleSections()`: Filtra e retorna apenas as seções acessíveis com base no progresso atual

**Seções registradas:**
- `Phase1MainSection`: Visão geral da Fase 1 (Overworld)
- `Phase1GoalsSection`: Objetivos de eliminação de mobs da Fase 1
- `Phase2MainSection`: Visão geral da Fase 2 (Nether)
- `Phase2GoalsSection`: Objetivos de eliminação de mobs da Fase 2
- `Phase3MainSection`: Visão geral da Fase 3 (End)
- `PartiesSection`: Gerenciamento e informações de grupos de jogadores
- `CustomPhasesSection`: Fases e objetivos customizados, incluindo integração com mods externos

**Integração:**
- Consumido pelo `ProgressionHUDScreen` para obter as seções a serem renderizadas
- Centraliza a lógica de gerenciamento de seções, permitindo fácil expansão do sistema

##### `Phase1MainSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase1MainSection`

Implementação da interface `HUDSection` que exibe informações gerais e objetivos principais da Fase 1 (Overworld).

**Funcionalidades principais:**
- Exibe o status atual dos objetivos especiais da Fase 1
- Mostra indicadores visuais para objetivos completos e pendentes
- Apresenta resumo do progresso de eliminação de mobs quando este recurso está habilitado
- Integra-se com bosses de mods externos classificados para a Fase 1

**Objetivos monitorados:**
- Elder Guardian: Boss aquático que deve ser derrotado
- Raid: Evento que deve ser vencido em uma vila
- Trial Vault: Conquista relacionada aos baús de trial chambers
- Voluntaire Exile: Conquista relacionada a pillagers/saqueadores
- Wither: Boss que deve ser derrotado
- Warden: Boss que deve ser derrotado

**Comportamento contextual:**
- Exibe mensagem de conclusão quando a Fase 1 está completa
- Informa que o Nether está desbloqueado após a conclusão
- Mostra mensagens de instrução quando os objetivos ainda estão pendentes
- Respeita as configurações do servidor quanto a requisitos habilitados/desabilitados

**Integração com mods externos:**
- Inicializa a integração com mods externos no lado do cliente
- Exibe bosses de mods externos categorizados como Fase 1
- Monitora o status de conclusão desses bosses adicionais

**Formatação visual:**
- Utiliza ícones consistentes (✔/❌) para indicar status de conclusão
- Aplica esquema de cores para destacar diferentes tipos de informação
- Organiza o conteúdo em seções claramente delimitadas

##### `Phase1GoalsSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase1GoalsSection`

Implementação da interface `HUDSection` focada nos objetivos de eliminação de mobs da Fase 1 (Overworld).

**Funcionalidades principais:**
- Exibe contadores detalhados de mobs eliminados na Fase 1
- Organiza os mobs em categorias: comuns, especiais e mobs-objetivo
- Mostra progresso atual versus requisitos configurados para cada tipo de mob
- Fornece resumo estatístico do progresso geral de eliminação

**Categorias de mobs:**
- **Mobs Comuns**: Zombie, Skeleton, Stray, Husk, Spider, Creeper, Drowned
- **Mobs Especiais**: Enderman, Witch, Pillager, Vindicator, Bogged, Breeze
- **Mobs-Objetivo**: Ravager, Evoker (considerados mais desafiadores)

**Estatísticas e resumo:**
- Total de eliminações versus requisito total
- Número de tipos de mobs completos versus total de tipos
- Formatação visual que indica progresso parcial ou conclusão

**Comportamento contextual:**
- Verifica se o sistema de eliminação de mobs está habilitado no servidor
- Exibe mensagens informativas quando o sistema está desabilitado
- Só mostra contadores para mobs que possuem requisitos configurados (> 0)
- Acessibilidade baseada na configuração do servidor e status da Fase 1

**Formatação visual:**
- Utiliza ícones de status (✔/⚔) para indicar conclusão ou progresso
- Aplica código de cores para diferentes estados (vermelho, amarelo, verde)
- Organiza os contadores de forma clara e consistente
- Agrupa mobs logicamente por tipo e dificuldade

##### `Phase3MainSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase3MainSection`

Implementação da interface `HUDSection` dedicada à Fase 3 (End), representando o estágio final da progressão.

**Funcionalidades principais:**
- Exibe os objetivos especiais relacionados à dimensão do End
- Foca em bosses de mods externos categorizados para a Fase 3
- Apresenta o status de conclusão dos desafios finais
- Fornece informações sobre o desafio definitivo e recompensas

**Comportamento contextual:**
- Verifica se a Fase 2 está completa para permitir acesso
- Exibe mensagem explicativa quando a Fase 3 está bloqueada
- Verifica se a Fase 3 deve ser exibida (configuração do servidor)
- Mostra mensagem de conclusão quando todos os objetivos do End estão completos

**Integração com mods externos:**
- Inicializa a integração com mods externos no lado do cliente
- Foca exclusivamente em bosses de mods classificados como Fase 3 (End)
- Exibe mensagem informativa quando não há bosses do End configurados
- Monitora o status de conclusão dos bosses do End adicionados por mods

**Informações adicionais:**
- Apresenta uma seção sobre os desafios únicos da dimensão do End
- Explica a natureza da exploração e dos bosses poderosos do End
- Menciona a existência de recompensas definitivas para a conclusão
- Fornece contexto para o estágio final da progressão do mod

## UI

#### HUDComponentManager
**Arquivo:** `net.mirai.dimtr.client.ui.HUDComponentManager`

Sistema de gerenciamento de componentes de interface de usuário para exibição em tempo real (overlay) durante o jogo.

**Funcionalidades principais:**
- Gerencia componentes de HUD modulares e independentes
- Permite ativação/desativação dinâmica de componentes específicos
- Posiciona automaticamente os componentes na tela com base em suas configurações
- Fornece renderização flexível com suporte a diferentes posicionamentos

**Arquitetura:**
- **Padrão Singleton**: Garante uma única instância para gerenciar todos os componentes
- **Componentes Modulares**: Cada elemento do HUD é um componente independente
- **Posicionamento Flexível**: Suporte a diferentes posições na tela (TOP_LEFT, TOP_RIGHT, etc.)
- **Renderização Callback**: Componentes definem sua própria lógica de renderização

**Componentes Padrão:**
- **Phase Progress**: Exibe o status de progresso das fases 1 e 2
- **Party Status**: Mostra informações sobre o grupo atual, incluindo número de membros e multiplicador
- **Mob Kills**: Lista os 5 mobs mais eliminados pelo jogador
- **Special Objectives**: Exibe objetivos especiais como Elder Guardian, Raid, Wither e Warden

**Sistema de Renderização:**
- Renderiza apenas componentes ativos e visíveis
- Calcula posicionamento dinâmico com base no tamanho da tela
- Evita recursão durante o processo de renderização
- Aplica formatação visual consistente (fundos semi-transparentes, cores para status)

**Classe HUDComponent:**
- Armazena propriedades como ID, nome, dimensões e posição
- Mantém uma referência ao renderizador e condição de visibilidade
- Implementa interface fluente para fácil configuração
- Utiliza functional interfaces para comportamento flexível

**Integração:**
- Consome dados de `ClientProgressionData` para informações de progresso
- Utiliza dados de `ClientPartyData` para exibir informações sobre grupos
- Chamado pelo sistema de renderização do Minecraft para exibir overlays

#### ClientEventHandlers
**Arquivo:** `net.mirai.dimtr.client.ClientEventHandlers`

Classe responsável por capturar e gerenciar eventos do lado do cliente, focada principalmente no mapeamento de teclas e interações com a interface.

**Funcionalidades principais:**
- Define e registra atalhos de teclado específicos do mod
- Gerencia o comportamento quando esses atalhos são pressionados
- Responde a eventos de jogo para abrir interfaces de usuário

**Estrutura:**
- **Classe Principal**: Contém definição e registro de teclas
- **Classe Aninhada**: `ClientGameEventHandlers` para eventos de jogo

**Mapeamento de Teclas:**
- Define `OPEN_HUD_KEY` como atalho principal (tecla J por padrão)
- Registra este atalho no sistema de eventos do Minecraft
- Agrupa atalhos na categoria personalizada "key.categories.dimtr"

**Manipulação de Eventos:**
- `onRegisterKeyMappings`: Registra teclas personalizadas durante inicialização
- `onClientTick`: Verifica se teclas específicas foram pressionadas durante o ciclo de jogo
- Implementa o método `consumeClick()` para evitar múltiplas ativações

**Comportamentos Implementados:**
- Abre a tela `ProgressionHUDScreen` quando a tecla J é pressionida
- Verifica se não há outra tela aberta antes de abrir o HUD
- Utiliza o Minecraft singleton para interações com o cliente

**Anotações e Implementação:**
- Utiliza `@EventBusSubscriber` para registrar manipuladores de eventos
- Separa eventos de inicialização (MOD bus) dos eventos de jogo (GAME bus)
- Restringe a execução apenas ao lado do cliente usando `Dist.CLIENT`

**Integração:**
- Conecta-se ao sistema de eventos do NeoForge para capturar eventos do jogo
- Interage com o sistema de interfaces do Minecraft para exibir telas
- Serve como ponto de entrada para interação do usuário com o sistema de progressão

### Dados de Parties Client

#### `ClientPartyData`
**Arquivo:** `net.mirai.dimtr.client.ClientPartyData`

Classe singleton que gerencia informações sobre grupos/parties no lado do cliente.

**Funcionalidades principais:**
- Armazenar dados sobre a party atual do jogador (ID, nome, membros, líder)
- Manter informações sobre o progresso compartilhado entre membros do grupo
- Gerenciar o multiplicador de requisitos baseado no tamanho do grupo
- Implementar sistema de cache para nomes de jogadores da party
- Suportar objetivos e fases customizadas compartilhadas

**Estrutura de dados:**
- **Dados da Party**: Identificador, nome, status público/privado, líder e membros
- **Progresso Compartilhado**: Estado de conclusão de objetivos principais compartilhados
- **Multiplicador**: Valor que determina o aumento de requisitos com base no tamanho do grupo
- **Cache de Nomes**: Mapeia UUIDs de jogadores para seus nomes para exibição consistente
- **Fases Customizadas**: Mapas para rastrear progresso compartilhado em conteúdo personalizado

**Funcionalidades específicas:**
- **Atualização de Dados**: Recebe e processa dados sincronizados do servidor
- **Gerenciamento de Cache**: Atualiza nomes de jogadores usando diferentes fontes de dados
- **Verificação de Estado**: Determina se o jogador está em uma party e seu papel
- **Acesso a Dados**: Fornece getters imutáveis para consumo seguro das informações
- **Proteção de Dados**: Implementa cópias defensivas para evitar modificações externas

**Comportamento contextual:**
- Detecta quando o jogador sai de uma party e limpa os dados correspondentes
- Mantém o cache de nomes mesmo quando jogadores estão offline/distantes
- Preserva apenas os dados relevantes no cache quando a composição do grupo muda

**Integração:**
- Conecta-se com o sistema de rede para receber atualizações do servidor
- Fornece dados para o sistema de UI (PartiesSection e HUD components)
- Influencia o comportamento de `ClientProgressionData` através do multiplicador de requisitos
- Fornece mecanismo para notificar atualizações de progresso do grupo

## Comandos

O sistema de comandos do Dimension Trials é dividido em duas categorias principais: comandos administrativos para gerenciamento de progressão individual e comandos de party para jogadores regulares.

### Pasta: `command`

#### `DimTrCommands`

**Arquivo:** `net.mirai.dimtr.command.DimTrCommands`

Sistema completo de comandos administrativos para gerenciamento de progressão individual e debug do mod.

**Estrutura hierárquica de comandos:**
```
/dimtr (requer OP level 2)
├── player <target>
│   ├── complete [phase1|phase2]
│   ├── reset [all|phase1|phase2|mob_kills]
│   ├── set [goal <name> <value>|mob_kill <type> <count>]
│   ├── status
│   └── sync
├── complete [phase1|phase2] (self-target)
├── reset [all|phase1|phase2|mob_kills] (self-target)
├── set [goal <name> <value>|mob_kill <type> <count>] (self-target)
├── status (self-target)
├── sync (self-target)
├── debug
│   ├── payload [target]
│   ├── global_status
│   ├── list_players
│   └── multipliers
└── systems
    ├── transfer [to_party|to_individual] <target>
    ├── custom_phase [reload|status <target>]
    └── boss_validation [reload|reputation <target>]
```

**Funcionalidades principais:**
- **Comandos Individuais para Jogadores Específicos:**
  - **Complete**: Força conclusão de fase específica com todos os requisitos preenchidos
  - **Reset**: Remove progresso de fases, objetivos ou contadores de mobs
  - **Set**: Define valores específicos para objetivos booleanos ou contadores de mobs
  - **Status**: Exibe relatório detalhado do progresso do jogador
  - **Sync**: Força sincronização de dados entre servidor e cliente

- **Comandos Self-Target:**
  - Versões dos comandos individuais que aplicam ao próprio executante
  - Suporte a pronomes traduzidos para feedback contextual
  - Mesma funcionalidade mas com validação simplificada

- **Sistema de Debug Avançado:**
  - **Payload Debug**: Analisa estrutura de dados de sincronização com detalhes técnicos
  - **Global Status**: Exibe configurações ativas do servidor e estatísticas gerais
  - **List Players**: Lista todos os jogadores online com seus status de progressão
  - **Multipliers**: Calcula e exibe multiplicadores individuais e próximos de cada jogador

- **Integração com Novos Sistemas:**
  - **Transfer System**: Migra progresso entre modos individual e party
  - **Custom Phase System**: Recarrega configurações e monitora status de fases personalizadas
  - **Boss Validation System**: Gerencia reputação e configurações de validação de boss kills

**Características técnicas:**
- Usa Brigadier para parsing de comandos com autocompleção
- Implementa validação robusta de argumentos e permissões
- Fornece feedback detalhado com formatação colorida
- Suporte completo a tradução de mensagens via Constants
- Integração com fogos de artifício para celebrações automáticas
- Cálculo dinâmico de requisitos da Fase 2 (125% dos valores originais)

**Tratamento de erros:**
- Validação de jogadores online/offline
- Verificação de permissões administrativas
- Tratamento de valores inválidos ou fora de faixa
- Logs detalhados para debugging de problemas

#### `PartyCommands`

**Arquivo:** `net.mirai.dimtr.command.PartyCommands`

Sistema completo de comandos para gerenciamento colaborativo de parties acessível a todos os jogadores.

**Estrutura hierárquica de comandos:**
```
/party (apenas jogadores)
├── create <nome> [senha]
├── join <nome> [senha]
├── leave
├── list
├── info
├── disband (apenas líder)
├── kick <jogador> (apenas líder)
├── promote <jogador> (apenas líder)
└── invite <jogador> (apenas líder)
```

**Funcionalidades principais:**
- **Gerenciamento de Criação e Entrada:**
  - **Create**: Cria party pública (sem senha) ou privada (com senha)
  - **Join**: Entrada em party pública ou privada com senha
  - **Leave**: Saída da party atual com transferência automática de progresso
  - Validação de nomes, senhas e disponibilidade de vagas
  - Sistema de resultados tipados para tratamento preciso de erros

- **Informações e Listagem:**
  - **List**: Exibe todas as parties públicas com status de vagas e multiplicadores
  - **Info**: Relatório detalhado da party atual incluindo:
    - Informações básicas (nome, tipo, membros, líder)
    - Multiplicador de requisitos ativo
    - Lista de membros com status online/offline
    - Progresso compartilhado em objetivos especiais
    - Contadores de mobs compartilhados (principais)
    - Comandos disponíveis baseados no papel do jogador

- **Comandos de Liderança:**
  - **Disband**: Dissolve a party e força saída de todos os membros
  - **Kick**: Remove jogador específico da party
  - **Promote**: Transfere liderança para outro membro
  - **Invite**: Convida jogadores para a party (com informações contextuais)
  - Validação rigorosa de permissões de liderança
  - Notificações automáticas para todos os membros afetados

**Integração com ProgressTransferService:**
- Transferência automática de progresso individual→party na entrada
- Transferência automática de progresso party→individual na saída
- Tratamento gracioso de erros de transferência com avisos informativos
- Preservação de dados em caso de falha na transferência

**Características técnicas:**
- Comandos sem requisito de OP (acessíveis a todos os jogadores)
- Uso de EntityArgument para seleção precisa de jogadores
- Sistema de resultados enumerados para controle de fluxo limpo
- Formatação rica com cores e emojis para feedback visual
- Cálculo dinâmico de multiplicadores baseado no tamanho do grupo
- Suporte completo a parties públicas e privadas

**Tratamento de contexto:**
- Diferenciação automática entre comandos para líder e membros
- Validação de estado da party antes de executar ações
- Verificação de limites de membros (máximo 4 por party)
- Prevenção de ações inválidas (kick próprio, promote próprio, etc.)
- Cache inteligente de nomes de jogadores online/offline

**Sistema de notificações:**
- Mensagens direcionadas para diferentes papéis (líder, membro específico, todos)
- Informações contextuais sobre benefícios de estar em party
- Dicas sobre comandos disponíveis baseadas no status do jogador
- Avisos e confirmações para ações irreversíveis

**Integração com outros sistemas:**
- PartyManager para operações de dados
- Constants para tradução consistente de mensagens
- ProgressTransferService para migração de progresso
- Sistemas de validação de nomes e senhas

## Sistema de Configuração

O sistema de configuração do Dimension Trials é altamente sofisticado e modular, permitindo personalização completa do mod através de múltiplas camadas de configuração.

### Pasta: `config`

#### `DimTrConfig`

**Arquivo:** `net.mirai.dimtr.config.DimTrConfig`

Classe principal de configuração do mod usando NeoForge ModConfigSpec para definir todas as configurações oficiais.

**Estrutura de configuração:**
```
DimTrConfig
├── Server (configurações do servidor)
│   ├── Phase Configuration (habilitação de fases)
│   ├── Phase 1 Special Objectives (objetivos especiais)
│   ├── Phase 2 Special Objectives (objetivos especiais)
│   ├── Phase 1 Mob Kill Requirements (requisitos de mobs)
│   ├── Phase 2 Mob Kill Requirements (requisitos de mobs)
│   ├── Difficulty Multipliers (multiplicadores de dificuldade)
│   ├── Party System Configuration (sistema de parties)
│   ├── Debug and Synchronization (debug e sincronização)
│   └── External Mod Integration (integração com mods externos)
└── Client (configurações do cliente)
    ├── Interface Configuration (interface geral)
    ├── HUD Configuration (configurações do HUD)
    └── Party Interface Configuration (interface de parties)
```

**Configurações do Servidor:**
- `enablePhase1/enablePhase2`: Habilita/desabilita fases individuais
- `enableMobKillsPhase1/enableMobKillsPhase2`: Controla requisitos de mobs por fase
- `enableMultipliers`: Ativa multiplicadores de dificuldade após progressão
- `enableXpMultiplier`: Aplica multiplicador de XP baseado na progressão

**2. Objetivos Especiais:**
- **Fase 1**: Elder Guardian, Raid, Trial Vault, Voluntary Exile
- **Fase 2**: Wither, Warden
- Cada objetivo pode ser habilitado/desabilitado individualmente

**3. Requisitos de Mobs:**
- **Fase 1**: 16 tipos de mobs do Overworld com valores configuráveis (0-1000)
- **Fase 2**: 7 tipos de mobs do Nether com valores configuráveis
- **Goal Kills especiais**: Ravager (1) e Evoker (5) como mobs raros/difíceis
- Todos os valores têm ranges de validação para prevenir configurações inválidas

**4. Sistema de Parties:**
- `maxPartySize`: Tamanho máximo de party (2-10, padrão 4)
- `partyProgressionMultiplier`: Multiplicador de progressão por membro adicional (0.0-2.0)
- `partyProximityRadius`: Raio para compartilhamento de progresso (0-256 blocos)

**5. Integração com Mods Externos:**
- Suporte automático para Mowzie's Mobs e L_Ender's Cataclysm
- Controle sobre obrigatoriedade vs opcionalidade de bosses externos
- Criação automática de Fase 3 para bosses do End de mods externos

**Configurações do Cliente:**
- `enableHUD/enableSounds/enableParticles`: Controles de interface básica
- `hudPosition`: Posição do HUD (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER)
- `hudScale`: Escala do HUD (0.5-2.0)
- `hudXOffset/hudYOffset`: Offsets de posicionamento (-1000 a 1000)

**Características técnicas:**
- Usa padrão Builder do NeoForge para type safety
- Enum `HUDPosition` para posições predefinidas do HUD
- Validação de ranges para todos os valores numéricos
- Comentários detalhados para cada configuração
- Separação clara entre configurações de servidor e cliente

#### `ConfigurationManager`

**Arquivo:** `net.mirai.dimtr.config.ConfigurationManager`

Gerenciador centralizado e robusto que unifica acesso a todas as configurações do mod, oferecendo funcionalidades avançadas de cache e extensibilidade.

**Funcionalidades principais:**
- **1. Sistema de Cache Inteligente:**
  - Cache com TTL de 30 segundos para otimização de performance
  - Invalidação automática e manual de cache
  - Thread-safety completo usando ReentrantReadWriteLock
  - Timestamps para controle de validade do cache

- **2. Acesso Unificado a Configurações:**
  - Suporte a tipos genéricos com type safety (`<T>`)
  - Fallbacks com valores padrão ou suppliers
  - Resolução automática entre configurações NeoForge e customizadas JSON
  - Navegação por hierarquia usando paths em dot-notation

- **3. Sistema de Configurações Customizadas:**
  - Suporte a arquivos JSON em `config/dimtr/custom/`
  - Carregamento automático de múltiplos arquivos de configuração
  - Conversão de tipos automática (Boolean, Integer, Double, String)
  - Estruturas hierárquicas complexas suportadas

- **4. Sistema de Observadores:**
  - Interface `ConfigChangeListener` para reagir a mudanças
  - Registro de observadores por path específico
  - Notificações automáticas com valores antigos e novos

**Métodos principais:**
```java
// Acesso básico com type safety
Optional<T> getConfig(String path, Class<T> type)
T getConfig(String path, Class<T> type, T defaultValue)
T getConfig(String path, Class<T> type, Supplier<T> defaultSupplier)

// Gerenciamento de cache e recarregamento
void reloadConfig(String path)
boolean hasConfig(String path)

// Sistema de observadores
void addConfigChangeListener(String path, ConfigChangeListener listener)
```

**Métodos de conveniência:**
- `isPartySystemEnabled()`, `getMaxPartySize()`, `getPartyProgressionMultiplier()`
- `getZombieKillRequirement()`, `getRavagerKillRequirement()`, `getEvokerKillRequirement()`
- Acesso direto a configurações frequentemente usadas

**Resolução de configurações:**
1. **NeoForge Config**: Busca primeiro em `DimTrConfig.SERVER`/`DimTrConfig.CLIENT`
2. **Custom Config**: Fallback para arquivos JSON customizados
3. **Type Conversion**: Conversão automática entre tipos compatíveis
4. **Error Handling**: Logging detalhado para troubleshooting

**Thread-safety e performance:**
- ReadWriteLock para acesso concorrente otimizado
- Cache concurrent-safe usando ConcurrentHashMap
- Inicialização lazy e shutdown controlado
- Estados de inicialização para prevenir uso incorreto

#### `CustomRequirements`

**Arquivo:** `net.mirai.dimtr.config.CustomRequirements`

Sistema avançado de requisitos personalizáveis que permite aos usuários criarem fases customizadas através de arquivos JSON, integrando perfeitamente com o sistema de parties.

**Funcionalidades principais:**
- **1. Sistema de Arquivos JSON:**
  - Diretório de configuração: `config/dimtr/custom_requirements/`
  - Criação automática de arquivo de exemplo (`example_requirements.json`)
  - Carregamento automático de múltiplos arquivos .json
  - Validação e error handling robusto

- **2. Estrutura de Dados Hierárquica:**
  - **CustomRequirementSet:**
    - `name`: Nome descritivo do conjunto de requisitos
    - `description`: Descrição detalhada
    - `enabled`: Flag para habilitar/desabilitar o conjunto
    - `customPhases`: Mapa de fases customizadas

  - **CustomPhase:**
    - `name/description`: Identificação e descrição da fase
    - `dimensionAccess`: Lista de dimensões que a fase libera acesso
    - `requiredPreviousPhases`: Dependências de fases anteriores
    - `specialObjectives`: Objetivos especiais customizados
    - `mobRequirements`: Requisitos de eliminação de mobs
    - `healthMultiplier/damageMultiplier/xpMultiplier`: Multiplicadores de dificuldade

  - **CustomObjective:**
    - `displayName/description`: Nome e descrição do objetivo
    - `required`: Se o objetivo é obrigatório ou opcional
    - `completed`: Status de conclusão

**3. Integração com Sistema de Parties:**
- `canPlayerAccessCustomDimension()`: Verifica acesso considerando progresso de party
- `getAdjustedCustomMobRequirement()`: Aplica multiplicadores de party aos requisitos
- `isCustomMobRequirementComplete()`: Validação de completude com contexto de party
- Transferência automática de progresso entre modos individual e party

**4. Controle de Acesso a Dimensões:**
- `findBlockingPhaseForDimension()`: Identifica qual fase bloqueia uma dimensão
- Verificação automática de progresso em parties vs individual
- Sistema de dependências entre fases customizadas

**Exemplos de uso (arquivo gerado automaticamente):**

**Phase 3 (Twilight Forest):**
```json
{
  "name": "Phase 3: Twilight Forest",
  "dimensionAccess": ["twilightforest:twilight_forest"],
  "requiredPreviousPhases": ["phase1", "phase2"],
  "specialObjectives": {
    "twilight_lich": {"displayName": "Lich Defeated", "required": true},
    "twilight_hydra": {"displayName": "Hydra Defeated", "required": true}
  },
  "mobRequirements": {
    "twilightforest:skeleton_druid": 15,
    "twilightforest:wraith": 10
  },
  "healthMultiplier": 2.5
}
```

**Phase 4 (Aether):**
```json
{
  "name": "Phase 4: Aether",
  "dimensionAccess": ["aether:the_aether"],
  "requiredPreviousPhases": ["phase1", "phase2", "phase3"],
  "specialObjectives": {
    "aether_slider": {"displayName": "Slider Defeated", "required": true}
  },
  "mobRequirements": {
    "aether:blue_swet": 20,
    "aether:cockatrice": 8
  },
  "healthMultiplier": 3.0
}
```

**Métodos de gerenciamento:**
- `loadCustomRequirements()`: Carregamento inicial de todos os arquivos
- `reload()`: Recarregamento dinâmico de configurações
- `saveCustomRequirement()`: Salvamento programático de novos requisitos
- `getAllCustomPhases()`: Acesso a todas as fases carregadas

**Características avançadas:**
- Desabilitação por padrão dos exemplos para evitar conflitos
- Sistema de logs detalhado para debugging
- Validação de JSON com fallback gracioso
- Suporte a mods opcionais (fases só carregam se os mods estão presentes)

**Integração com outros sistemas:**
- Conecta com `ProgressionManager` para verificação de progresso
- Usa `PartyManager` para contexto de party
- Integra com `CustomPhaseSystem` para execução em tempo real
- Suporta `Constants` para mensagens de log consistentes

## Data

### Sistema de Coordenação de Progressão

O sistema de dados do Dimension Trials é organizado em coordenadores especializados que implementam a arquitetura modular extraída do antigo `ProgressionCoordinator` monolítico. Cada coordenador tem responsabilidades específicas e bem definidas, garantindo separação de responsabilidades e melhor manutenibilidade.

### Pasta: `data`

#### `CustomPhaseCoordinator`

**Arquivo:** `net.mirai.dimtr.data.CustomPhaseCoordinator`

Coordenador especializado para processamento de fases customizadas, extraído como parte da refatoração arquitetural para modularizar responsabilidades.

**✅ RESPONSABILIDADES PRINCIPAIS:**
- Processar objetivos de fases customizadas
- Processar kills de mobs customizados
- Verificar completion de fases customizadas
- Coordenar entre sistemas de party e individual
- Aplicar multiplicadores de requisitos para parties

**🎯 INTEGRAÇÃO:**
- Trabalha com sistema de Custom Requirements
- Usa SyncManager para atualizações em lote
- Conecta com PartyManager e ProgressionManager

**Thread-Safety:**
- Implementa `ReentrantLock` para operações críticas
- Garante processamento thread-safe em ambiente de servidor

**Métodos Principais:**
**`processCustomObjective(UUID playerId, String phaseId, String objectiveId, ServerLevel serverLevel)`**
- Coordena processamento entre party e individual
- Primeiro tenta processar para party se jogador estiver em uma
- Fallback para processamento individual
- Retorna `true` se foi processado com sucesso

**`processCustomMobKill(UUID playerId, String phaseId, String mobType, ServerLevel serverLevel)`**
- Similar ao processamento de objetivos, mas para kills de mobs
- Implementa mesma lógica de coordenação party → individual
- Registra debug logs quando habilitado

**`canPlayerAccessCustomDimension(UUID playerId, String dimensionString, ServerLevel serverLevel)`**
- Verifica se jogador pode acessar dimensão customizada
- Identifica fase bloqueante usando `CustomRequirements.findBlockingPhaseForDimension()`
- Verifica progresso em party primeiro, depois individual
- Retorna `true` se dimensão não é controlada ou requisitos atendidos

**Métodos Privados de Processamento:**
**Party Processing:**
- `processPartyCustomObjective()`: Marca objetivo como completo para toda a party
- `processPartyCustomMobKill()`: Incrementa kill compartilhado e registra contribuição
- `checkAndCompleteCustomPhase()`: Verifica se todos os requisitos da fase foram atendidos
- Aplica multiplicadores de party aos requisitos usando `party.getAdjustedRequirement()`

**Individual Processing:**
- `processIndividualCustomObjective()`: Processa objetivo para jogador individual
- `processIndividualCustomMobKill()`: Incrementa kill individual
- `checkAndCompleteCustomPhaseIndividual()`: Verifica completion individual
- Sem multiplicadores (requisitos base)

**Verificação de Requisitos:**
- Itera sobre `mobRequirements` e `specialObjectives` das fases customizadas
- Compara progresso atual vs requisitos (com multiplicadores para parties)
- Marca fase como completa quando todos os requisitos são atendidos
- Envia notificações para membros da party

**Sistema de Notificações:**
- `notifyPartyPhaseCompletion()`: Notifica todos os membros sobre conclusão
- Logs informativos para tracking de progresso
- Placeholders para sistema de notificação customizado futuro

**Características Avançadas:**
- Debug logging extensivo quando habilitado
- Sincronização com `SyncManager.scheduleFullSync()` para atualizações críticas
- Integração seamless com `CustomRequirements` para definições de fases
- Suporte a objetivos opcionais vs obrigatórios

---

#### `ExternalBossCoordinator`

**Arquivo:** `net.mirai.dimtr.data.ExternalBossCoordinator`

Coordenador especializado para processamento de bosses de mods externos (Cataclysm, Mowzie's Mobs, etc.), implementando integração seamless com o sistema de progressão.

**✅ RESPONSABILIDADES PRINCIPAIS:**
- Processar morte de bosses externos
- Verificar completion de fases baseado em bosses externos
- Coordenar entre sistemas de party e individual
- Aplicar sincronização imediata para eventos críticos

**🎯 INTEGRAÇÃO:**
- Trabalha junto com `ExternalModIntegration` para detectar bosses
- Usa `SyncManager` para atualizações imediatas
- Verifica configuração `enableExternalModIntegration`

**Thread-Safety:**
- Implementa `ReentrantLock` para operações críticas
- Processamento thread-safe de eventos críticos de boss kills

**Método Principal:**
**`processExternalBossKill(UUID playerId, String bossEntityId, int phase, ServerLevel serverLevel)`**
- Verifica se integração com mods externos está habilitada
- Coordena entre processamento party vs individual
- Converte `bossEntityId` para `objectiveKey` (substitui ":" por "_")
- Retorna `true` se processado com sucesso

**Fluxo de Processamento:**
**Party Processing:**
- `processPartyExternalBoss()`: Marca boss como derrotado para toda a party
- Armazena no sistema de custom objectives: `party.setSharedCustomObjectiveComplete("external_bosses", objectiveKey, true)`
- Backup individual: também marca no progresso individual do jogador
- Verifica completion de fase correspondente
- **🚨 SINCRONIZAÇÃO IMEDIATA:** `SyncManager.forceSync()` para eventos críticos
- Notifica todos os membros da party sobre o boss derrotado

**Individual Processing:**
- `processIndividualExternalBoss()`: Processa para jogador individual
- Verifica se boss já estava marcado como completo
- Marca como completo: `playerData.setCustomObjectiveComplete("external_bosses", objectiveKey, true)`
- Verifica completion de fase correspondente
- **🚨 SINCRONIZAÇÃO IMEDIATA:** `SyncManager.forceSync()` para o jogador

**Verificação de Completion de Fases:**
**Party Mode:**
- `checkPhaseCompletionWithExternalBosses()`: Verifica fases 1, 2 e 3
- `isPhase1CompleteWithExternalBosses()`: Objetivos padrão + bosses externos obrigatórios
- `isPhase2CompleteWithExternalBosses()`: Wither + Warden + bosses externos obrigatórios
- `isPhase3CompleteWithExternalBosses()`: Apenas bosses externos (se habilitado)

**Individual Mode:**
- `isPhase1CompleteWithExternalBossesIndividual()`: Equivalente individual da Fase 1
- `isPhase2CompleteWithExternalBossesIndividual()`: Equivalente individual da Fase 2
- Verifica progresso individual vs objetivos padrão + bosses externos

**Sistema de Bosses por Fase:**
- Usa `ExternalModIntegration.getBossesForPhase(phase)` para obter lista de bosses
- Verifica flag `boss.required` para determinar se é obrigatório
- Fase 3 só existe se `createPhase3ForEndBosses` estiver habilitado

**Notificações:**
- `notifyPartyMembersOfBossKill()`: Notifica membros sobre boss derrotado
- `notifyPartyPhaseCompletion()`: Notifica sobre conclusão de fase
- Placeholders para sistema de notificação futuro

**Query de Status:**
- `isExternalBossComplete(UUID playerId, String bossEntityId, ServerLevel serverLevel)`: Verifica se boss foi derrotado
- Primeiro verifica progresso de party, depois individual
- Usado para queries externas de status

**Características Especiais:**
- **Eventos Críticos:** Usa `forceSync()` em vez de `scheduleFullSync()` para bosses
- **Configurabilidade:** Respeita `enableExternalModIntegration` global
- **Flexibilidade:** Suporta bosses opcionais vs obrigatórios
- **Logs Detalhados:** Debug logging extensivo para troubleshooting

---

#### `IndividualProgressionCoordinator`

**Arquivo:** `net.mirai.dimtr.data.IndividualProgressionCoordinator`

Coordenador especializado para progressão individual, extraído do ProgressionCoordinator monolítico para melhor separação de responsabilidades.

**✅ RESPONSABILIDADES PRINCIPAIS:**
- Processar mob kills individuais
- Processar objetivos especiais individuais
- Verificar completion de fases para jogadores individuais
- Controlar acesso a dimensões (individual)

**Thread-Safety:**
- Usa `synchronized (PROCESSING_LOCK)` para operações críticas
- Garante processamento thread-safe em contexto individual

**Métodos de Processamento:**
**`processIndividualMobKill(UUID playerId, String mobType, ServerLevel serverLevel)`**
- Incrementa kill individual usando `playerData.incrementMobKill(mobType)`
- Marca `ProgressionManager` como dirty
- Verifica completion de fases após cada kill
- Debug logging para tracking

**`processIndividualSpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel)`**
- Processa objetivos especiais usando switch com constantes:
  - `OBJECTIVE_TYPE_ELDER_GUARDIAN`: Elder Guardian derrotado
  - `OBJECTIVE_TYPE_RAID`: Raid vencida
  - `OBJECTIVE_TYPE_TRIAL_VAULT`: Trial Vault advancement
  - `OBJECTIVE_TYPE_VOLUNTARY_EXILE`: Voluntary Exile advancement  
  - `OBJECTIVE_TYPE_WITHER`: Wither derrotado
  - `OBJECTIVE_TYPE_WARDEN`: Warden derrotado
- Retorna `true` apenas se foi newly completed
- Verifica completion de fases após cada objetivo

**Métodos de Acesso:**
- `processMobKill()`: Alias para `processIndividualMobKill()`
- `processSpecialObjective()`: Alias para `processIndividualSpecialObjective()`
- `canPlayerAccessDimension()`: Verifica acesso individual a dimensões

**Verificação de Completion de Fases:**
**`checkPhaseCompletionForPlayer(PlayerProgressionData playerData, ProgressionManager progressionManager, ServerLevel serverLevel)`**
- Verifica Fase 1 e Fase 2 sequencialmente
- Marca fases como completas quando requisitos atendidos
- Envia notificações i18n para o jogador
- Logs informativos de completion

**Fase 1 - `isPhase1CompleteForPlayer()`:**
- **Objetivos Especiais:**
  - Elder Guardian derrotado
  - Raid vencida
  - Trial Vault: Conquista relacionada aos baús de trial chambers
  - Voluntaire Exile: Conquista relacionada a pillagers/saqueadores (se `reqVoluntaryExile` habilitado)
- **Mob Kills (se `enableMobKillsPhase1` habilitado):**
  - Verifica todos os mobs da Fase 1 usando `checkPhase1MobKills()`
  - Mobs incluem: zombie, skeleton, spider, creeper, witch, pillager, captain, vindicator, bogged, breeze, ravager, evoker

**Fase 2 - `isPhase2CompleteForPlayer()`:**
- **Objetivos Especiais:**
  - Wither derrotado
  - Warden derrotado
- **Mob Kills (se `enableMobKillsPhase2` habilitado):**
  - Verifica mobs da Fase 2 usando `checkPhase2MobKills()`
  - **Nether Mobs:** blaze, wither_skeleton, piglin_brute, hoglin, zoglin, ghast, piglin
  - **Overworld Mobs Repetidos:** 125% dos requisitos da Fase 1 (`Math.ceil(overworldBase * 1.25)`)
- Usa configuração do `DimTrConfig.SERVER` para todos os valores

**Sistema de Requisitos:**
**`getRequiredKills(String mobType, int phase)`**
- **Fase 1:** Retorna requisitos base diretamente da configuração
- **Fase 2:** 
  - Nether mobs: requisitos específicos da configuração
  - Overworld mobs: 125% dos requisitos da Fase 1 (`Math.ceil(overworldBase * 1.25)`)
- Usa configuração do `DimTrConfig.SERVER` para todos os valores

**Verificação de Mob Kills:**
- `checkPhase1MobKills()`: Itera array de mobs da Fase 1, verifica cada requisito
- `checkPhase2MobKills()`: Itera array de mobs da Fase 2, inclui lógica de 125%
- Usa `playerData.getMobKillCount(mobType)` para obter progresso atual
- Retorna `false` imediatamente se qualquer requisito não atendido

**Notificações:**
- Usa `I18nHelper.sendMessage()` para notificações localizadas
- Keys: "progression.phase1.complete", "progression.phase2.complete"
- Envia apenas para o jogador específico (não party)

**Características Especiais:**
- **Sem Multiplicadores:** Usa requisitos base (diferente de parties)
- **Verificação Individual:** Não considera progresso de party
- **Logs Detalhados:** Debug e info logging para tracking
- **Configuração Dinâmica:** Respeita todas as configurações de habilitação de requisitos

---

#### `PartyManager`

**Arquivo:** `net.mirai.dimtr.data.PartyManager`

Gerenciador centralizado para parties/grupos, implementando sistema completo de criação, entrada, saída e coordenação entre progresso individual e de party. Estende `SavedData` para persistência automática.

**✅ RESPONSABILIDADES PRINCIPAIS:**
- Gerenciar ciclo de vida completo de parties (criar, entrar, sair, deletar)
- Coordenar transferência de progresso entre individual ↔ party
- Processar objectives e mob kills em contexto de party
- Sincronizar dados de party para todos os membros
- Implementar sistema de backup e restore de dados

**Estrutura de Dados:**
- `parties`: Map de UUID → PartyData (todas as parties ativas)
- `playerToParty`: Map de UUID → UUID (mapeamento jogador → party)
- `serverForContext`: Referência ao MinecraftServer para operações

**🎯 MÉTODOS PRINCIPAIS DE GERENCIAMENTO:**
**`createParty(UUID leaderId, String partyName, String password)`**
- **Validações:**
  - Verifica se jogador já está em party
  - Valida nome (max 20 chars, não vazio, único)
  - Gera UUID único para nova party
- **Transferência de Progresso:**
  - Obtém progresso individual do líder via `ProgressionManager`
  - Transfere mob kills usando `party.transferIndividualProgress()`
  - Transfere objetivos especiais usando `transferSpecialObjectives()`
  - **🎯 NOVO:** Inclui transferência de Custom Phases
- **Resultado:** Enum `CreatePartyResult` (SUCCESS, ALREADY_IN_PARTY, INVALID_NAME, NAME_TAKEN)

**`joinParty(UUID playerId, String partyName, String password)`**
- **Busca e Validações:**
  - Encontra party por nome (case-insensitive)
  - Verifica senha e capacidade máxima
  - Previne entrada em parties cheias
- **Integração de Progresso:**
  - Similar à criação: transfere todo o progresso individual para party
  - Preserva progresso existente da party (usa maior valor)
  - Atualiza mapeamento jogador → party
- **Resultado:** Enum `JoinPartyResult` (SUCCESS, ALREADY_IN_PARTY, PARTY_NOT_FOUND, WRONG_PASSWORD, PARTY_FULL)

**`leaveParty(UUID playerId)`**
- **🎯 CORREÇÃO CRÍTICA:** Sincronização antes da remoção
  - Envia dados vazios para cliente via `sendEmptyPartyDataToClient()`
  - Limpa HUD de party antes de processar saída
- **Restauração de Progresso:**
  - Remove contribuições individuais via `party.removeIndividualContributions()`
  - Restaura mob kills individuais via `progressionManager.restorePlayerMobKills()`
  - **🎯 NOVO:** Restaura objetivos especiais via `restoreSpecialObjectivesToPlayer()`
  - Restaura Custom Phases para o jogador
- **Limpeza Automática:**
  - Deleta party se ficar vazia
  - Sincroniza membros restantes se party continuar existindo
- **Resultado:** Enum `LeavePartyResult` (SUCCESS, NOT_IN_PARTY)

**🎯 INTEGRAÇÃO COM PROGRESSÃO:**
**`processPartyMobKill(UUID playerId, String mobType, ServerLevel serverLevel)`**
- Valida se jogador está em party
- Incrementa kill compartilhado via `party.incrementSharedMobKillWithContribution()`
- **🎆 NOVO: Sistema de Celebração:**
  - Verifica completion de fases pela primeira vez
  - Lança fogos de artifício para todos os membros usando `NotificationHelper.launchCelebrationFireworks()`
  - Phase 1: fogos tipo 1, Phase 2: fogos tipo 2
- Sincroniza party para todos os membros

**`processPartySpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel)`**
- **Switch de Objetivos:**
  - `elder_guardian`, `raid`, `trial_vault`, `voluntary_exile`, `wither`, `warden`
  - Marca objetivo como completo para party inteira
- **Sincronização Cruzada:**
  - Atualiza progressão individual de TODOS os membros da party
  - Garante que todos tenham o objective individual também
  - Usa `ProgressionManager.updateXXXKilled(memberId)` para cada membro

**🎯 MÉTODOS AUXILIARES PARA TRANSFERÊNCIA:**
**`transferSpecialObjectives(PartyData party, PlayerProgressionData playerData)`**
- **Fase 1:** elderGuardian, raid, trialVault, voluntaryExile
- **Fase 2:** wither, warden  
- **Status de Fases:** phase1Completed, phase2Completed
- **🎯 NOVO:** Chama `party.transferCustomProgressFromPlayer()` para Custom Phases

**`restoreSpecialObjectivesToPlayer(UUID playerId, PartyData party)`**
- **LÓGICA:** Se party tem objetivo, todos os membros devem tê-lo ao sair
- **Aplicação:** Usa métodos públicos do ProgressionManager
- Garante que progresso da party seja preservado no jogador individual
- **🎯 NOVO:** Inclui `party.restoreCustomProgressToPlayer()` para Custom Phases

**Sistema de Networking:**
**`syncPartyToMembers(UUID partyId)`**
- Cria `UpdatePartyToClientPayload` completo com todos os dados:
  - Informações básicas (ID, nome, líder, membros)
  - Progresso compartilhado (mob kills, objectives)
  - **🎯 NOVO:** Dados de Custom Phases (completion, mob kills, objectives)
- Envia para todos os membros online usando `PacketDistributor.sendToPlayer()`

**`sendEmptyPartyDataToClient(ServerPlayer player)`**
- **✅ CORREÇÃO:** Limpa HUD do cliente quando jogador sai
- Envia payload vazio para resetar interface
- Previne dados stale na interface do cliente

**Sistema de Listagem:**
**`getPublicParties()`**
- Filtra parties públicas (`PartyData.isPublic()`)
- Retorna `List<PartyInfo>` com informações essenciais:
  - Nome, membros atuais, máximo de membros, status público

**Backup e Restore:**
**`serializeForBackup()`**
- Serializa todos os dados para CompoundTag
- Inclui timestamp para tracking
- Prepara dados para sistema de backup externo

**`deserializeFromBackup(CompoundTag backupTag)`**
- Restaura dados de backup com validação rigorosa
- Verifica integridade de UUIDs
- Remove mapeamentos órfãos (players em parties inexistentes)
- Logs detalhados do processo de restore

**Características Avançadas:**
- **Persistência Automática:** Como `SavedData`, salva automaticamente
- **Thread-Safety:** Operações sincronizadas onde necessário  
- **Integridade de Dados:** Validação extensiva em todas as operações
- **Backward Compatibility:** Sistema de serialização robusto
- **Debug Support:** Logs detalhados para troubleshooting
- **Configuração Dinâmica:** Respeita configurações de party no servidor

---

#### `SyncManager`

**Arquivo:** `net.mirai.dimtr.sync.SyncManager`

**Responsabilidade Principal:**
Gerenciador centralizado de sincronização para todos os sistemas do mod. Resolve problemas de sincronização fornecendo coordenação central entre progressão individual e party, garantindo notificação adequada dos clientes através de sistema de batching eficiente.

**Características Principais:**
1. **Sistema de Batching Inteligente:**
   - Scheduler de thread única para processamento em lote
   - Delays configuráveis: 1s para sync normal, 100ms para sync forçado
   - Reduz overhead de rede agrupando atualizações

2. **Thread-Safety Completa:**
   - `ReentrantLock` para operações de sincronização
   - Sets thread-safe (`ConcurrentHashMap.newKeySet()`) para filas pendentes
   - Coordenação segura entre múltiplas threads

3. **Tipos de Sincronização:**
   - **Progressão Individual:** `PENDING_PROGRESSION_SYNC`
   - **Dados de Party:** `PENDING_PARTY_SYNC`
   - **Informações de Fase:** `PENDING_PHASE_SYNC`
   - **Sync Forçado:** `PENDING_FORCE_SYNC` (alta prioridade)

4. **Scheduler Duplo:**
   - Procesador de batching regular (`processPendingSyncs()`)
   - Procesador de alta prioridade (`processForcedSyncs()`)
   - Thread daemon dedicada para não bloquear shutdown

**Métodos de Agendamento:**
- `scheduleProgressionSync(UUID)`: Agenda sync de progressão individual
- `schedulePartySync(UUID)`: Agenda sync de dados de party
- `schedulePhaseSync(UUID)`: Agenda sync de informações de fase
- `scheduleFullSync(UUID)`: Agenda todos os tipos de sync
- `schedulePartyMembersSync(PartyData)`: Agenda sync para todos membros da party
- `forceSync(UUID)`: Força sincronização imediata (eventos críticos)

**Integrações:**
- `ProgressionManager`: Obtenção de dados de progressão individual
- `PartyManager`: Obtenção de dados de party
- `DimTrConfig.SERVER`: Configurações de debug logging
- Payloads de rede: `UpdateProgressionToClientPayload`, `UpdatePartyToClientPayload`
- `MinecraftServer`: Obtenção de instância do servidor e lista de jogadores

**Características Avançadas:**
- Sistema de logging condicional baseado em configuração
- Métodos de inicialização e shutdown seguros
- API de estatísticas para debugging (`getSyncStats()`)
- Tratamento robusto de erros com try-catch
- Design preparado para integração futura com sistema de rede

**Estado de Implementação:**
- Core functionality implementado
- Métodos de criação de payload comentados (aguardando integração final)
- Estrutura preparada para obtenção automática do servidor
- Sistema de batching completamente funcional

**Processamento em Lote:**

1. **Processamento Regular:**
   - `processPendingSyncs()`: Executado a cada 1 segundo
   - Processa filas de progressão, party e fase em sequência
   - Cria cópias thread-safe das filas antes de processar
   - Limpa filas após processamento para evitar reenvios

2. **Processamento de Alta Prioridade:**
   - `processForcedSyncs()`: Executado a cada 100ms
   - Sincronização imediata para eventos críticos (boss kills)
   - Processamento completo (progressão + party)
   - Logging especial para tracking de operações críticas

**Métodos Internos de Sincronização:**

1. **Sincronização Individual:**
   - `syncPlayerProgression()`: Envia dados de progressão individual
   - `syncPlayerParty()`: Envia dados de party do jogador
   - `syncPlayerPhase()`: Envia informações de fase (usa progressão por ora)
   - `syncPlayerComplete()`: Sincronização completa (progressão + party)

2. **Preparação para Sistema de Rede:**
   - Métodos comentados para criação de payloads (`createProgressionPayload`)
   - Estrutura pronta para `UpdateProgressionToClientPayload`
   - Preparação para `UpdatePartyToClientPayload`
   - Integration points com `PacketDistributor.sendToPlayer()`

**Gerenciamento de Estado:**
1. **Inicialização e Shutdown:**
   - `initialize()`: Inicia schedulers e configura threads daemon
   - `shutdown()`: Para schedulers com timeout de 5 segundos
   - `isInitialized()`: Verificação de estado para prevenção de uso incorreto
   - Cleanup automático de filas pendentes no shutdown

2. **API de Estatísticas:**
   - `getSyncStats()`: Retorna contadores de filas pendentes
   - Útil para debugging e monitoring de performance
   - Formato: "Pending syncs - Progression: X, Party: Y, Phase: Z, Force: W"

**Controle de Threading:**
1. **Single Thread Scheduler:**
   - Usa `Executors.newSingleThreadScheduledExecutor()` para serialização
   - Thread nomeada "DimTr-SyncManager" para identificação
   - Daemon thread para shutdown automático
   - Schedule de intervalos fixos para consistência

2. **Lock Management:**
   - `ReentrantLock SYNC_LOCK` para operações críticas
   - Lock acquire/release dentro de try-finally para cleanup garantido
   - Previne condições de corrida durante processamento
   - Coordenação segura entre scheduling e processamento

**Características de Performance:**
1. **Batching Inteligente:**
   - Agrupa múltiplas mudanças em uma única sincronização
   - Reduz overhead de rede significativamente
   - Evita spam de packets para mudanças rápidas
   - Delays configuráveis para different priorities

2. **Memory Management:**
   - Usa `Set.copyOf()` para snapshots imutáveis
   - Limpa filas imediatamente após processamento
   - Evita memory leaks com cleanup automático
   - Design concurrent-safe sem excessive locking

**Integrações Futuras:**
- Sistema de rede completo com payloads tipados
- Métricas avançadas de performance
- Configuração dinâmica de intervals de batching
- Support para custom sync strategies por tipo de dados
- Integration com sistema de backup para sync pré-operações críticas

## System

### Sistema de Backup e Validação

O pacote `system` contém sistemas críticos de backup automático e validação de integridade para o mod.

#### `BackupManager`

**Arquivo:** `net.mirai.dimtr.system.BackupManager`

**Responsabilidade Principal:**
Sistema completo de backup automático e manual para dados do Dimension Trials. Oferece backup programado, restauração, compressão automática e rotação de arquivos antigos para garantir integridade dos dados.

**Características Principais:**
1. **Backup Automático Programado:**
   - Scheduler executando backups a cada 2 horas
   - Thread daemon dedicada para não impactar performance
   - Backup automático com razão "auto" documentada

2. **Sistema de Compressão:**
   - Usa `GZIPOutputStream` para compressão automática
   - Formato `.dat.gz` para economia de espaço
   - Serialização via `NbtIo.writeCompressed()`

3. **Rotação Automática de Backups:**
   - Máximo de 30 backups mantidos (`MAX_BACKUPS`)
   - Remoção automática dos backups mais antigos
   - Ordenação por data de modificação para gestão eficiente

4. **Thread-Safety:**
   - `ReentrantReadWriteLock` para controle de concorrência
   - Read locks para consultas, write locks para modificações
   - Operações atômicas para backup e restauração

**Funcionalidades de Backup:**
1. **Criação de Backup:**
   - `createBackup(String reason)`: Backup manual com razão
   - `createBackup(String reason, UUID playerId)`: Backup relacionado a jogador específico
   - Timestamp formato: `yyyyMMdd_HHmmss`
   - Metadata completa incluindo versão do mod

2. **Estrutura dos Dados:**
   - **Header:** Razão, timestamp, versão do mod, playerId opcional
   - **Progression:** Dados serializados do `ProgressionManager`
   - **Party:** Dados serializados do `PartyManager`
   - Formato NBT para compatibilidade com Minecraft

3. **Restauração:**
   - `restoreBackup(String backupId)`: Restaura backup específico
   - Backup automático do estado atual antes de restaurar
   - Busca inteligente por ID (com/sem extensão)
   - Sincronização automática de todos jogadores online

**Gestão e Consulta:**
1. **Listagem de Backups:**
   - `listBackups()`: Lista todos backups com metadata
   - Classe `BackupInfo` com ID, razão, timestamp e tamanho
   - Ordenação por data (mais recente primeiro)

2. **Limpeza Automática:**
   - `pruneOldBackups()`: Remove backups excedentes
   - Preserva os 30 backups mais recentes
   - Log de backups removidos para auditoria

**Integrações:**
- `ProgressionManager`: Serialização/deserialização de dados de progressão
- `PartyManager`: Serialização/deserialização de dados de party
- `MinecraftServer`: Obtenção de world/level e lista de jogadores
- `DimTrMod.LOGGER`: Sistema de logging centralizado

**Características Avançadas:**
- **Backup pré-restauração para prevenir perda de dados**
- **Sistema de validação de integridade via `NbtAccounter`**
- **Sincronização automática pós-restauração**
- **Design modular para fácil extensão**
- **Tratamento robusto de erros com logging detalhado**

#### `BossKillValidator`

**Arquivo:** `net.mirai.dimtr.system.BossKillValidator`

**Responsabilidade Principal:**
Sistema avançado de validação de boss kills com detecção anti-cheat, sistema de reputação de jogadores e validação rigorosa de legitimidade. Garante que apenas boss kills legítimos sejam contabilizados para progressão.

**Arquitetura de Classes:**

1. **BossValidationConfig:**
   - Configuração específica por boss (Ender Dragon, Wither, Elder Guardian, Warden)
   - Requisitos: dano direto, dimensão válida, distância mínima, limite de tempo
   - Validações customizadas por boss (ex: cristais do End destruídos)
   - Configuração de assistência permitida e limites

2. **PlayerReputation:**
   - Sistema de pontuação de reputação (0-100)
   - Contadores de kills legítimos, suspeitos e inválidos
   - Auto-blacklist para jogadores com reputação baixa
   - Histórico temporal de atividade

3. **BossKillRecord:**
   - Registro completo de cada tentativa de boss kill
   - Metadata: localização, dimensão, fonte de dano, timestamp
   - Resultado de validação com detalhes técnicos
   - Histórico limitado a 50 registros por jogador

4. **ValidationResult:**
   - Status: VALID, SUSPICIOUS, INVALID
   - Nível de confiança (0.0-1.0)
   - Razão detalhada e metadata adicional
   - Sistema de scoring baseado em múltiplos fatores

**Sistema de Validação Multi-Layer:**

1. **Validação Básica:**
   - Verificação de parâmetros nulos
   - Check de blacklist de jogadores
   - Verificação de lista de suspeitos
   - Configuração de boss válida

2. **Validação Contextual:**
   - **Fonte de Dano:** Verificação de dano direto do jogador
   - **Dimensão:** Validação de dimensão permitida por boss
   - **Distância:** Verificação de distância mínima/máxima
   - **Reputação:** Fator de reputação do jogador

3. **Detecção de Padrões Suspeitos:**
   - Múltiplos kills do mesmo boss em pouco tempo
   - Intervalos muito curtos entre boss kills
   - Análise temporal de atividade (última hora)
   - Sistema de flags automáticas

4. **Validações Customizadas por Boss:**
   - **Ender Dragon:** Cristais do End destruídos
   - **Elder Guardian:** Localização em Ocean Monument
   - **Warden:** Bioma Deep Dark e nível de luz
   - **Wither:** Spawn natural vs artificial

**Configurações Padrão por Boss:**

1. **Ender Dragon:**
   - Dimensão: The End apenas
   - Distância mínima: 10 blocos
   - Tempo limite: 10 minutos
   - Assistência: até 3 jogadores
   - Requer: Cristais destruídos

2. **Wither:**
   - Dimensões: Overworld, Nether
   - Distância mínima: 5 blocos  
   - Tempo limite: 5 minutos
   - Assistência: até 2 jogadores
   - Spawn artificial permitido

3. **Elder Guardian:**
   - Dimensão: Overworld apenas
   - Distância mínima: 8 blocos
   - Tempo limite: 3 minutos
   - Assistência: até 4 jogadores
   - Requer: Ocean Monument

4. **Warden:**
   - Dimensão: Overworld apenas
   - Distância mínima: 6 blocos
   - Tempo limite: 4 minutos  
   - Assistência: 1 jogador apenas
   - Requer: Deep Dark, luz = 0

**Sistema de Confiança (Confidence Scoring):**
- Inicia com 100% de confiança
- Redução por falhas de validação:
  - Dano indireto: -30%
  - Dimensão inválida: -40%
  - Distância excessiva: -20%
  - Padrão suspeito: -30%
  - Reputação baixa: multiplicador baseado em score

**Integrações:**
- `DeltaUpdateSystem`: Notificação de boss kills válidos
- `BatchSyncProcessor`: Adição a batch de alta prioridade
- `ConfigurationManager`: Carregamento de configurações customizadas
- Sistema de logging: Auditoria completa de tentativas

**Anti-Cheat Features:**
- Detecção de farm automatizado

#### `CustomPhaseSystem`

**Arquivo:** `net.mirai.dimtr.system.CustomPhaseSystem`

**Responsabilidade Principal:**
Sistema ativo de gerenciamento dinâmico de fases personalizadas, conectado com processamento de eventos em tempo real. Permite criação e execução de fases completamente customizadas baseadas em configuração dinâmica.

**Arquitetura de Classes:**
1. **PhaseDefinition:**
   - Definição completa de uma fase customizada
   - Campos: phaseId, displayName, description, enabled, priority
   - Requisitos: mobKills (Map<String, Integer>), objectives (Map<String, Boolean>)
   - Advancements obrigatórios e requisitos customizados extensíveis
   - Sistema de prioridade para ordenação de fases

2. **PhaseProgress:**
   - Progresso individual de um jogador em uma fase específica
   - Tracking: currentMobKills, currentObjectives, completedAdvancements
   - Progresso customizado extensível via customProgress Map
   - Porcentagem de conclusão e timestamp de completion
   - Método `copy()` para thread-safety

3. **PhaseChangeListener:**
   - Interface para listeners de mudanças de fase
   - Eventos: onPhaseCompleted, onPhaseProgressUpdated, onPhaseStarted
   - Permite integração modular com outros sistemas

**Sistema de Thread-Safety:**
- `ReentrantReadWriteLock` para operações de leitura/escrita
- Cache thread-safe via `ConcurrentHashMap`
- Listeners gerenciados com thread-safety
- Processamento atômico de eventos críticos

**Processamento de Eventos em Tempo Real:**
1. **Mob Kill Processing:**
   - `processMobKill(ServerPlayer, LivingEntity, DamageSource)`
   - Conectado diretamente com eventos de morte de mobs
   - Incremento automático de kill counts para fases relevantes
   - Verificação automática de completion após cada kill
   - Notificação de progresso via listeners

2. **Advancement Processing:**
   - `processAdvancementEarned(ServerPlayer, AdvancementHolder)`
   - Conectado com sistema de achievements do Minecraft
   - Tracking automático de advancements por fase
   - Verificação de completion baseada em lista de requisitos

3. **Objective Processing:**
   - `processObjectiveCompleted(ServerPlayer, String, boolean)`
   - Sistema genérico para objetivos especiais customizados
   - Suporte a objetivos obrigatórios vs opcionais
   - Integração com sistema de custom objectives

**Sistema de Configuração Dinâmica:**
1. **Carregamento de Configuração:**
   - `loadPhaseDefinitions()`: Carrega do `ConfigurationManager`
   - Parsing seguro com validação de tipos
   - Fallback para fases padrão (hardcoded)
   - Recarregamento dinâmico suportado

2. **Parsing Robusto:**
   - `parsePhaseDefinition()`: Converte Map para PhaseDefinition
   - Verificação de tipos rigorosa para evitar crashes
   - Defaults sensatos para campos opcionais
   - Tratamento de erros com logging detalhado

3. **Fases Padrão:**
   - **Phase 1 Extended:** Versão estendida da Fase 1 com requisitos customizados
   - **Boss Master:** Derrotar todos os bosses principais
   - Exemplos funcionais para demonstração do sistema

**Verificação de Completion Avançada:**
1. **Algoritmo Multi-Critério:**
   - Verificação de mob kills vs requisitos
   - Verificação de objectives (boolean matching)
   - Verificação de advancements obrigatórios
   - Validação de requisitos customizados extensíveis

2. **Cálculo de Porcentagem:**
   - `calculateCompletionPercentage()`: Cálculo proporcional
   - Consideração de todos os tipos de requisitos
   - Atualização em tempo real conforme progresso

3. **Requisitos Customizados:**
   - Sistema extensível via `checkCustomRequirements()`
   - Exemplos: tempo de jogo, visitas a dimensões
   - Framework para adição de novos tipos de validação

**API Pública Completa:**
1. **Consulta de Progresso:**
   - `getPlayerPhaseProgress(UUID)`: Todas as fases de um jogador
   - `getPhaseProgress(UUID, String)`: Fase específica
   - `isPhaseCompleted(UUID, String)`: Check de completion
   - Retorna cópias para thread-safety

2. **Consulta de Configuração:**
   - `getPhaseDefinitions()`: Todas as definições carregadas
   - Acesso read-only às configurações
   - Informações para UI e debugging

3. **Gerenciamento de Dados:**
   - `clearPlayerData(UUID)`: Limpeza de dados específicos
   - `addPhaseChangeListener()`: Registro de listeners
   - APIs para integração com outros sistemas

**Integrações:**
1. **ConfigurationManager:**
   - Carregamento de fases customizadas via JSON
   - Sistema de configuração centralizado
   - Suporte a recarregamento dinâmico

2. **DeltaUpdateSystem:**
   - Envio de atualizações de progresso via delta
   - Integração com sistema de networking
   - Alta prioridade para mudanças de fase

3. **Sistema de Eventos:**
   - Conectado com eventos nativos do Minecraft
   - ProcessingPipeline para eventos de mob kill
   - Integration com advancement system

**Características Avançadas:**
- Sistema de prioridade para fases
- Suporte a objetivos opcionais vs obrigatórios
- Extensibilidade via custom requirements
- Logging detalhado para debugging
- Design modular para fácil extensão
- Performance otimizada com caching inteligente

#### `DataValidator`

**Arquivo:** `net.mirai.dimtr.system.DataValidator`

**Responsabilidade Principal:**
Sistema abrangente de validação de integridade para todos os dados do mod. Detecta inconsistências, corrupção e problemas de dados entre diferentes sistemas, fornecendo relatórios detalhados para diagnóstico.

**Arquitetura de Validação:**
1. **Classe ValidationIssue:**
   - Severidade: WARNING, ERROR, CRITICAL
   - Descrição detalhada do problema encontrado
   - Sistema de categorização para priorização
   - ToString formatado para logging

2. **Níveis de Validação:**
   - **CRITICAL:** Problemas que impedem funcionamento
   - **ERROR:** Inconsistências sérias que afetam gameplay
   - **WARNING:** Problemas potenciais que podem causar problemas

**Métodos de Validação:**
1. **Validação Completa:**
   - `validateAll()`: Executa todas as validações
   - Validação de progressão individual
   - Validação de dados de party
   - Validação de consistência entre sistemas
   - Relatório consolidado com contagem de problemas

2. **Validações Específicas:**
   - `validateProgressionOnly()`: Apenas dados de progressão
   - `validatePartyOnly()`: Apenas dados de party
   - Execução isolada para debugging específico

**Validação de Dados de Progressão:**
1. **Verificações de Integridade:**
   - Detecção de valores negativos (kill counts impossíveis)
   - Verificação de progressão lógica (Fase 2 sem Fase 1)
   - Detecção de valores extremamente altos (possível corrupção)
   - Validação de consistência interna

2. **Problemas Detectados:**
   - Kill counts negativos para qualquer mob
   - Fase 2 completa sem Fase 1 completa
   - Valores suspeitos (>10.000 kills)
   - Estados impossíveis de progressão

**Validação de Dados de Party:**
1. **Verificações Estruturais:**
   - Parties vazias (que deveriam ter sido removidas)
   - Líderes que não são membros da party
   - Kill counts negativos compartilhados
   - Fase 2 completa sem Fase 1 (nível party)

2. **Verificações de Mapeamento:**
   - Jogadores mapeados para parties inexistentes
   - Inconsistência entre mapeamento e membership
   - Órfãos em estruturas de dados
   - Referências circulares ou quebradas

**Validação Cross-System:**
1. **Consistência Entre Sistemas:**
   - `validateCrossSystemConsistency()`: Validação entre ProgressionManager e PartyManager
   - Verificação de objetivos especiais em ambos os sistemas
   - Detecção de divergências entre progresso individual e party
   - Validação de integridade referencial

**Características Técnicas:**
1. **Acesso a Managers:**
   - Requer instância de `MinecraftServer` no construtor
   - Acesso a overworld level para obter managers
   - Verificação de disponibilidade antes de validar

2. **Tratamento de Erros:**
   - Verificações de null safety em todos os pontos
   - Graceful degradation quando managers não disponíveis
   - Logging detalhado de problemas encontrados

3. **Sistema de Relatórios:**
   - Relatório estruturado com severidade e descrição
   - Contagem de problemas por categoria
   - Logging automático com símbolos distintivos (✅⚠️❌)

**Limitações Atuais:**
- Requer implementação de métodos `getAllPlayerData()` e `getAllParties()` nos managers
- Validação cross-system básica (espaço para expansão)
- Sistema de auto-correção não implementado

**Casos de Uso:**
- Diagnóstico de problemas reportados por jogadores
- Verificação de integridade após migrações/updates
- Debugging de problemas de sincronização
- Manutenção preventiva de dados
- Validação antes de backups importantes

**Integrações:**
- `ProgressionManager`: Acesso a dados de progressão individual
- `PartyManager`: Acesso a dados de party e mapeamentos
- `MinecraftServer`: Contexto para acesso a world level
- Sistema de logging: Relatórios detalhados de problemas

#### `ProgressTransferService`

**Arquivo:** `net.mirai.dimtr.system.ProgressTransferService`

**Responsabilidade Principal:**
Sistema avançado de transferência bidirecional de progresso entre modos individual e party. Implementa algoritmos de merge inteligentes, validação de consistência e histórico completo de transferências com capacidade de rollback.

**Arquitetura de Classes:**
1. **TransferRecord:**
   - Registro completo de cada transferência realizada
   - Estados antes e depois para rollback capability
   - Timestamp, tipo de transferência e razão documentada
   - Flag de sucesso para filtrar operações válidas

2. **TransferType Enum:**
   - PARTY_TO_INDIVIDUAL: Transferência party → individual
   - INDIVIDUAL_TO_PARTY: Transferência individual → party
   - PARTY_MERGE: Merge de progresso entre membros
   - ROLLBACK: Reversão de transferência anterior

3. **MergeStrategy Enum:**
   - TAKE_HIGHEST/TAKE_LOWEST: Valor maior/menor
   - SUM_VALUES: Soma de valores
   - LOGICAL_OR: OR lógico para booleans
   - KEEP_INDIVIDUAL/KEEP_PARTY: Preservar origem específica

4. **TransferConfig:**
   - Configuração de merge strategies por campo
   - Flags: validateConsistency, createBackup, notifyPlayer
   - Estratégias padrão para diferentes tipos de dados

**Sistema de Thread-Safety:**
- `ReentrantReadWriteLock` para operações críticas
- Write locks para transferências, read locks para consultas
- Histórico thread-safe via `ConcurrentHashMap`
- Rate limiting com timestamps thread-safe

**Algoritmos de Merge Inteligentes:**
1. **Estratégias por Tipo de Dado:**
   - **Mob Kills:** SUM_VALUES (soma kills individuais e party)
   - **Objetivos Especiais:** LOGICAL_OR (se qualquer um tem, todos têm)
   - **Fases Completadas:** LOGICAL_OR (preservar progresso)
   - **Custom Data:** Merge específico por tipo

2. **Merge de Custom Maps:**
   - Custom mob kills: soma de valores
   - Custom objectives: OR lógico
   - Custom phase completion: OR lógico
   - Preservação de estrutura hierárquica

3. **Reflection-Based Merging:**
   - `applyMergeStrategy()`: Acesso dinâmico a campos
   - Tipo-safe operations com verificação de tipos
   - Fallback gracioso para campos não encontrados

**Funcionalidades Principais:**
1. **Transferência Party → Individual:**
   - `transferFromPartyToIndividual()`: Transferência com configuração
   - Calcula progresso agregado de todos membros da party
   - Merge com progresso individual existente
   - Validação de consistência pós-merge

2. **Transferência Individual → Party:**
   - `transferFromIndividualToParty()`: Aplica progresso a todos membros
   - Backup automático antes da aplicação
   - Merge individual com cada membro da party
   - Sincronização de todos os membros

3. **Sincronização Completa de Party:**
   - `synchronizePartyProgress()`: Merge bidirecional
   - Coleta progresso de todos os membros
   - Calcula estado agregado otimizado
   - Aplica resultado a todos os membros

4. **Sistema de Rollback:**
   - `rollbackLastTransfer()`: Reversão da última operação
   - Busca último registro válido no histórico
   - Aplicação do estado anterior
   - Registro de rollback para auditoria

**Rate Limiting e Controle:**
1. **Proteção Contra Spam:**
   - Intervalo mínimo: 30 segundos entre transferências
   - `checkRateLimit()`: Verificação baseada em timestamp
   - `canTransfer()`: API pública para verificação

2. **Histórico Limitado:**
   - Máximo 50 registros por jogador
   - Remoção automática de registros antigos
   - Preservação de dados críticos para rollback

**Validação e Integridade:**
1. **Validação de Consistência:**
   - `validateProgressionConsistency()`: Verificações básicas
   - Detecção de valores negativos
   - Validação de lógica de fases (Fase 2 requer Fase 1)
   - Prevenção de estados impossíveis

2. **Backup Automático:**
   - Estados antes/depois preservados em TransferRecord
   - Histórico para auditoria e debugging
   - Capacidade de rollback para estado conhecido

**Sistema de Notificações:**
1. **Integração com Networking:**
   - `notifyProgressTransfer()`: Notifica via DeltaUpdateSystem
   - Batch processing via `BatchSyncProcessor`
   - Alta prioridade para transferências (priority 8)

2. **Tipos de Notificação:**
   - Progress transfer por tipo
   - Delta updates para clientes
   - Logs detalhados para auditoria

**API Pública:**
1. **Métodos de Transferência:**
   - Versões simples com configuração padrão
   - Versões avançadas com TransferConfig customizado
   - Support para transferências com razão documentada

2. **Consulta e Controle:**
   - `getTransferHistory(UUID)`: Histórico completo
   - `clearPlayerData(UUID)`: Limpeza de dados
   - `canTransfer(UUID)`: Verificação de rate limiting

**Características Avançadas:**
- Algoritmos de merge configuráveis por campo
- Sistema de histórico completo com rollback
- Rate limiting inteligente
- Validação de integridade multi-layer
- Integração seamless com sistema de networking
- Design extensível para novos tipos de dados
- Performance otimizada com caching e batching

#### `StateRecoveryManager`

**Arquivo:** `net.mirai.dimtr.system.StateRecoveryManager`

**Responsabilidade Principal:**
Sistema robusto de recuperação de estado que oferece proteção automática contra corrupção de dados, criação de snapshots antes de operações críticas e restauração automática para último estado estável conhecido.

**Características Principais:**
1. **Snapshots de Estado Pré-Operação:**
   - `createStateSnapshot(String operationName)`: Snapshot assíncrono
   - Execução em thread separada para não bloquear gameplay
   - Timestamp e nome da operação para identificação
   - CompletableFuture para controle de execução

2. **Sistema de Estado Estável:**
   - Verificação automática a cada 15 minutos
   - Validação via `DataValidator` antes de marcar como estável
   - Sobrescrita apenas quando estado atual é 100% válido
   - Arquivo `last_stable_state.dat.gz` para recuperação

3. **Recovery Automático:**
   - `performRecoveryIfNeeded()`: Baseado em resultados de validação
   - Critérios: problemas CRITICAL ou 3+ erros
   - Backup automático do estado atual antes de recovery
   - Sincronização automática de todos jogadores online

**Sistema de Validação Integrado:**
1. **Critérios para Recovery:**
   - Presença de problemas CRITICAL
   - 3 ou mais problemas ERROR
   - Análise automática de severidade dos problemas
   - Decisão inteligente baseada em contexto

2. **Recovery Process:**
   - Backup do estado atual (`pre_recovery`)
   - Carregamento do último estado estável
   - Aplicação aos managers (Progression e Party)
   - Sincronização automática de jogadores online

**Gestão de Arquivos:**
1. **Estrutura de Snapshots:**
   - Prefixo `pre_` + nome da operação + timestamp
   - Formato comprimido `.dat.gz`
   - Metadata completa: operação, timestamp
   - Integração com `BackupManager` para storage

2. **Estado Estável:**
   - Arquivo único sobrescrito: `last_stable_state.dat.gz`
   - Validation automática antes de sobrescrever
   - Metadata de validação incluída
   - Compressão automática para economia de espaço

**Scheduler Automático:**
1. **Verificações Periódicas:**
   - Thread daemon dedicada para não afetar performance
   - Intervalo: 15 minutos para verificação de estado
   - `checkAndUpdateStableState()`: Validação automática
   - Atualização de estado estável apenas se válido

2. **Thread Management:**
   - Single thread scheduler para serialização
   - Daemon thread para shutdown automático
   - Timeout de 5 segundos para shutdown gracioso

**Integrações:**
1. **DataValidator:**
   - `validateAll()`: Validação completa para critérios de recovery
   - Análise de severidade para decisão automática
   - Integration para verificação de estado estável

2. **Manager Integration:**
   - `ProgressionManager.serializeForBackup()`: Serialização
   - `PartyManager.serializeForBackup()`: Backup de party data
   - `deserializeFromBackup()`: Restauração completa
   - Sincronização automática via `sendToClient()`

3. **Sistema de Backup:**
   - Leverage do `BackupManager` para operações críticas
   - Criação de backups antes de recovery
   - Sharing de padrões de naming e storage

**Características Técnicas:**
1. **Async Operations:**
   - CompletableFuture para snapshots não-bloqueantes
   - Execução em thread pool para performance
   - Error handling robusto com completion garantida

2. **NBT Serialization:**
   - Formato nativo do Minecraft para compatibilidade
   - Compressão GZIP para economia de espaço
   - NbtAccounter para controle de memória

3. **Error Handling:**
   - Try-catch abrangente em todas as operações críticas
   - Logging detalhado com símbolos distintivos
   - Graceful degradation quando recovery falha

**API Pública:**
1. **Snapshot Management:**
   - `createStateSnapshot()`: Para operações críticas
   - CompletableFuture return para controle de timing
   - Suporte a operações nomeadas para tracking

2. **Recovery Control:**
   - `performRecoveryIfNeeded()`: Análise automática
   - `restoreLastStableState()`: Recovery manual
   - Integration com validation results

3. **Lifecycle Management:**
   - `initialize(MinecraftServer)`: Setup completo
   - `shutdown()`: Cleanup de recursos
   - Thread management automático

**Casos de Uso:**
- Proteção antes de operations críticas (party operations, data migration)
- Recovery automático após crashes ou corrupção
- Manutenção preventiva de integridade de dados
- Debugging de problemas de estado
- Rollback para estados conhecidos válidos

**Características Avançadas:**
- Recovery totalmente automático baseado em validação
- Sistema de snapshots pré-operação
- Integration completa com validation pipeline
- Thread-safety e performance otimizada
- Logging detalhado para auditoria e debugging

## Utilidades e Constantes

### Pasta: `util`

O pacote `util` contém classes utilitárias essenciais que oferecem suporte de performance, cache de configurações e constantes centralizadas para todo o mod.

#### `BlockPosPool`

**Arquivo:** `net.mirai.dimtr.util.BlockPosPool`

**Responsabilidade Principal:**
Sistema de object pooling para `BlockPos.MutableBlockPos` visando reduzir overhead de Garbage Collection durante verificações frequentes de posições, especialmente em verificações de portal.

**Características Principais:**
1. **Object Pooling Pattern:**
   - Pool de 100 instâncias `BlockPos.MutableBlockPos` pré-criadas
   - `ArrayBlockingQueue` thread-safe para gerenciamento do pool
   - Inicialização estática do pool no carregamento da classe

2. **API de Acquire/Release:**
   - `acquire()`: Obtém instância do pool ou cria nova se pool vazio
   - `acquire(int x, int y, int z)`: Obtém instância configurada com coordenadas
   - `acquire(BlockPos original)`: Obtém instância baseada em outro BlockPos
   - `release(MutableBlockPos pos)`: Retorna instância para reutilização

3. **Gerenciamento de Estado:**
   - Reset automático para (0,0,0) antes de retornar ao pool
   - Verificação de capacidade para evitar overflow do pool
   - Proteção contra null values

**Otimizações de Performance:**
- Evita criação repetitiva de objetos BlockPos em loops críticos
- Reduz pressure no Garbage Collector significativamente
- Thread-safe via `BlockingQueue` para uso em ambiente multiplayer
- Pool size configurado para balance entre memória e performance

**Características Técnicas:**
- Pool size fixo de 100 instâncias (`POOL_SIZE`)
- Usa `BlockPos.MutableBlockPos` para modificação in-place
- Fallback gracioso criando nova instância se pool esgotado
- API de debugging via `getAvailablePositions()`

**Casos de Uso:**
- Verificações de portal (Nether/End)
- Cálculos de proximidade entre jogadores
- Iterações sobre blocos em área
- Qualquer operação que precise de coordenadas temporárias

#### `ConfigCache`

**Arquivo:** `net.mirai.dimtr.util.ConfigCache`

**Responsabilidade Principal:**
Sistema de cache de configurações críticas para evitar múltiplas chamadas custosas ao `DimTrConfig`, melhorando performance em loops críticos e operações frequentes.

**Características Principais:**
1. **Cache de Configurações Críticas:**
   - **Flags Booleanas:** enablePartySystem, enableDebugLogging, enablePhase1/2, enableMobKills
   - **Valores Numéricos:** maxPartySize, partyProgressionMultiplier, partyProximityRadius
   - **Inicialização Lazy:** Cache só é populado quando primeiro acessado

2. **Sistema de Thread-Safety:**
   - Todas as variáveis são `volatile` para visibilidade entre threads
   - Inicialização thread-safe via `ensureInitialized()`
   - Cache pode ser atualizado via `refreshCache()` quando configurações mudam

3. **API de Acesso Otimizada:**
   - `isPartySystemEnabled()`: Cache de enablePartySystem
   - `isDebugLoggingEnabled()`: Cache de enableDebugLogging  
   - `isPhase1Enabled()` / `isPhase2Enabled()`: Cache de habilitação de fases
   - `isMobKillsPhase1Enabled()` / `isMobKillsPhase2Enabled()`: Cache de requisitos de mobs
   - `getMaxPartySize()`: Cache de tamanho máximo de party
   - `getPartyProgressionMultiplier()`: Cache de multiplicador de progressão
   - `getPartyProximityRadius()`: Cache de raio de proximidade

**Funcionalidades Avançadas:**
1. **Métodos de Conveniência:**
   - `isCustomPhasesSystemEnabled()`: Sempre retorna true por padrão
   - `isExternalModIntegrationEnabled()`: Acesso seguro com fallback
   - `getConfigSafe<T>()`: Método genérico para acesso seguro a qualquer config

2. **Error Handling Robusto:**
   - Try-catch em métodos críticos para prevenir crashes
   - Fallbacks sensatos quando configuração não está disponível
   - Graceful degradation mantendo funcionalidade mesmo com config corrompida

**Características de Performance:**
- **Elimina Overhead:** Evita chamadas repetitivas a `.get()` em `ModConfigSpec`
- **Cache Local:** Valores armazenados em variáveis locais para acesso instantâneo
- **Lazy Loading:** Inicialização apenas quando necessário
- **Memory Efficient:** Cache pequeno com apenas valores críticos

**Integração:**
- Usado extensivamente por sistemas que fazem checks frequentes de configuração
- Integration points em `PartyManager`, `ProgressionManager`, sistemas de sincronização
- Atualização manual via `refreshCache()` quando configurações mudam dinamicamente

**Limitações e Considerações:**
- Cache não é automaticamente invalidado quando configurações mudam
- Requer chamada manual de `refreshCache()` após mudanças de config
- Valores cached podem ficar stale se não atualizados adequadamente

#### `Constants`

**Arquivo:** `net.mirai.dimtr.util.Constants`

**Responsabilidade Principal:**
Classe centralizada que define todas as constantes usadas throughout o mod, incluindo chaves de tradução, identificadores, configurações padrão e strings hardcoded. Serve como single source of truth para todos os valores constantes.

**Estrutura Organizacional:**
1. **Identificadores Básicos:**
   - `MOD_ID` / `MODID`: Identificador do mod ("dimtr")
   - `PROGRESSION_DATA_NAME`: Nome da chave de dados de progressão
   - Compatibilidade mantida entre versões antigas e novas

2. **Sistema de Tradução I18n:**
   - **HUD System:** 50+ chaves para interface do HUD
   - **GUI System:** 80+ chaves para elementos de interface
   - **Command System:** 60+ chaves para comandos e respostas
   - **Party System:** 100+ chaves para sistema de parties
   - **Notification System:** 20+ chaves para notificações

**Categorias Principais de Constantes:**
1. **HUD System (🎯 HUD SYSTEM):**
   - `HUD_TITLE`, `HUD_PHASE1_TITLE`, `HUD_PHASE2_TITLE`: Títulos das fases
   - `HUD_ELDER_GUARDIAN`, `HUD_WITHER_KILLED`, etc.: Objetivos especiais com emojis
   - `HUD_MOB_ZOMBIE`, `HUD_MOB_SKELETON`, etc.: Nomes de mobs com emojis
   - `HUD_SECTION_*`: Cabeçalhos de seções organizacionais

2. **Party System (🎯 SISTEMA DE PARTIES):**
   - **Success Messages:** PARTY_CREATE_SUCCESS, PARTY_JOIN_SUCCESS, etc.
   - **Error Messages:** PARTY_ERROR_*, com categorização detalhada
   - **Info Display:** PARTY_INFO_*, para exibição de informações
   - **Commands:** Constantes para todos os comandos de party

3. **Command System (🎯 DIMTR COMMANDS):**
   - **Admin Commands:** CMD_ADMIN_*, para comandos administrativos
   - **Player Commands:** CMD_PLAYER_*, para comandos de jogadores
   - **Debug System:** CMD_DEBUG_*, para sistema de debugging
   - **Help System:** HELP_*, para sistema de ajuda

4. **Mob Types & Objectives (🎯 MOB TYPES / OBJECTIVE TYPES):**
   - **Phase 1 Mobs:** MOB_TYPE_ZOMBIE, MOB_TYPE_SKELETON, etc.
   - **Phase 2 Mobs:** MOB_TYPE_BLAZE, MOB_TYPE_WITHER_SKELETON, etc.
   - **Special Objectives:** OBJECTIVE_TYPE_ELDER_GUARDIAN, etc.

**Características Avançadas:**
1. **Sistema de Ícones e Formatação:**
   - **Status Icons:** ICON_COMPLETED (✅), ICON_PENDING (⏳), etc.
   - **UI Formatting:** PROGRESS_SEPARATOR, LABEL_VALUE_SEPARATOR
   - **Progress Bar Elements:** PROGRESS_BAR_START, PROGRESS_BAR_FILLED, etc.
   - **Notification Icons:** Por tipo de notificação

2. **Configurações e Defaults:**
   - **Performance:** DEFAULT_PROXIMITY_RADIUS, DEFAULT_SYNC_INTERVAL_TICKS
   - **Party System:** DEFAULT_MAX_PARTY_SIZE, DEFAULT_PARTY_PROGRESSION_MULTIPLIER
   - **Gameplay:** DEFAULT_PHASE2_OVERWORLD_MULTIPLIER (125%)

3. **Logging System (🎯 LOGGING SYSTEM):**
   - **Initialization:** LOG_INITIALIZING_MOD, LOG_CONFIG_REGISTERED, etc.
   - **Feature Descriptions:** LOG_FEATURE_*, para documentar funcionalidades
   - **Debug Processing:** LOG_MOB_KILL_PARTY, LOG_OBJECTIVE_INDIVIDUAL, etc.

**Organização por Funcionalidade:**
1. **Translation Keys vs Hardcoded Strings:**
   - **Translation Keys:** Para texto que aparece para usuários
   - **Hardcoded Strings:** Para logging interno e debugging
   - **Migration Section:** Strings hardcoded que devem se tornar translation keys

2. **Command Literals:**
   - `CMD_PARTY_LITERAL`: "party"
   - `CMD_CREATE_LITERAL`: "create", `CMD_JOIN_LITERAL`: "join", etc.
   - Centralizados para evitar typos e facilitar mudanças

3. **Dimension & Advancement IDs:**
   - `DIMENSION_TYPE_NETHER`, `DIMENSION_TYPE_END`, `DIMENSION_TYPE_CUSTOM`
   - `ADVANCEMENT_VOLUNTARY_EXILE`, `ADVANCEMENT_HERO_OF_VILLAGE`, etc.

**Características Técnicas:**
1. **Massive Scale:** 1176+ linhas de constantes organizadas
2. **Categorização Rigorosa:** Comentários com emojis para fácil navegação
3. **Backward Compatibility:** Mantém constantes antigas para compatibilidade
4. **Future-Proofing:** Seções preparadas para migração de hardcoded strings

**Padrões e Convenções:**
- **Naming:** UPPER_SNAKE_CASE para todas as constantes
- **Prefixing:** Prefixos consistentes por categoria (HUD_, PARTY_, CMD_, etc.)
- **Documentation:** Comentários organizacionais com emojis distintivos
- **Grouping:** Agrupamento lógico com separadores visuais

**Integration Points:**
- **I18nHelper:** Usa essas chaves para tradução
- **Command System:** Referencia literals e message keys
- **GUI System:** Usa chaves de tradução para interface
- **Logging:** Usa constants para mensagens consistentes
- **All Managers:** Referenciam constants para strings padronizadas

**Estado de Migração:**
- **Complete:** Sistemas de HUD, GUI, Party em translation keys
- **In Progress:** Migração de strings hardcoded para translation keys
- **Future:** Expansão para suporte completo a múltiplos idiomas

Esta classe é fundamental para a manutenibilidade e internacionalização do mod, centralizando todas as strings e valores constantes em um local organizadoe facilmente acessível.

---

#### `PartyData`

**Arquivo:** `net.mirai.dimtr.data.PartyData`

Classe fundamental que representa os dados de uma party/grupo, implementando sistema robusto de progressão compartilhada, contribuições individuais e coordenação entre membros. Versão expandida que suporta fases customizadas e tracking granular de progresso.

**✅ CARACTERÍSTICAS PRINCIPAIS:**
- **Progressão Compartilhada:** Kills de mobs e objetivos especiais são compartilhados entre todos os membros
- **Contribuições Individuais:** Sistema de tracking granular que preserva o progresso individual ao entrar/sair da party
- **Multiplicadores Dinâmicos:** Requisitos escalam baseado no número de membros (75% adicional por membro extra)
- **Suporte a Fases Customizadas:** Totalmente integrado com o sistema de Custom Requirements
- **Transferência de Progresso:** Sistema bidirecional de transferência entre progresso individual e party

**🎯 ESTRUTURA DE DADOS:**

**Metadados da Party:**
- `UUID partyId`: Identificador único da party
- `String name`: Nome da party (modificável)
- `String password`: Senha para parties privadas (null = pública)
- `boolean isPublic`: Flag explícita de visibilidade
- `UUID leaderId`: Líder atual (transferível automaticamente)
- `Set<UUID> members`: Membros atuais (máximo 10)

**Sistema de Progressão Compartilhada:**
- `Map<String, Integer> sharedMobKills`: Kills de mobs das Fases 1 e 2
- `Map<UUID, Map<String, Integer>> individualContributions`: 🎯 **NOVO**: Tracking de contribuições por membro
- Objetivos especiais: `elderGuardianKilled`, `raidWon`, `trialVaultAdvancementEarned`, `voluntaireExileAdvancementEarned`, `witherKilled`, `wardenKilled`
- Status de fases: `phase1SharedCompleted`, `phase2SharedCompleted`

**🎯 Sistema de Fases Customizadas:**
- `Map<String, Boolean> sharedCustomPhaseCompletion`: Completion de fases customizadas
- `Map<String, Map<String, Integer>> sharedCustomMobKills`: Kills de mobs por fase customizada
- `Map<String, Map<String, Boolean>> sharedCustomObjectiveCompletion`: Objetivos customizados por fase

**MÉTODOS DE GESTÃO DE MEMBROS:**
- **Adição e Remoção de Membros: `addMember(UUID playerId)` e `removeMember(UUID playerId)`**
  - Adiciona ou remove membro da party
  - Atualiza automaticamente o líder se necessário
  - Inicializa ou remove contribuições individuais

**🎯 SISTEMA DE MULTIPLICADORES:**
- **Cálculo de Requisitos: `getRequirementMultiplier()` e `getAdjustedRequirement(int baseRequirement)`**
  - Calcula multiplicador baseado no número de membros
  - Aplica a lógica de 75% adicional por membro extra
  - Ajusta requisitos de mobs e objetivos especiais

**SISTEMA DE CONTRIBUIÇÕES INDIVIDUAIS:**
- **Transferência e Remoção de Progresso: `transferIndividualProgress(UUID playerId, Map<String, Integer> playerProgress)` e `removeIndividualContributions(UUID playerId)`**
  - Transfere ou remove progresso individual de um jogador na party
  - Atualiza kills compartilhados e registra/remover contribuições individuais

**VERIFICAÇÃO DE ACESSO A DIMENSÕES:**
- **Métodos `canAccessNether()` e `canAccessEnd()`**
  - Verificam se a party completou as fases necessárias para acessar Nether ou End
  - Respeitam as configurações de habilitação de fases

**VERIFICAÇÃO DE COMPLETION DE FASES:**
- **Métodos `isPhase1Complete()` e `isPhase2Complete()`**
  - Verificam se todos os objetivos especiais e requisitos de mobs foram atendidos
  - Usam a lógica de multipliers para ajustar os requisitos

**🎯 SISTEMA DE FASES CUSTOMIZADAS:**
- **Métodos de Verificação e Modificação de Fases e Objetivos Customizados**
  - `isCustomPhaseComplete(String phaseId)`, `setCustomPhaseComplete(String phaseId, boolean complete)`
  - `isCustomObjectiveComplete(String phaseId, String objectiveId)`, `setCustomObjectiveComplete(String phaseId, String objectiveId, boolean complete)`
  - `getCustomMobKills(String phaseId, String mobType)`, `incrementCustomMobKill(String phaseId, String mobType)`
  - Transferência bidirecional de progresso customizado entre party e individual

**SERIALIZAÇÃO NBT:**
- **Completa:** Serializa todos os dados incluindo contribuições individuais e fases customizadas
- **Versionada:** Suporte a compatibilidade backward para dados antigos
- **Robusta:** Tratamento de UUIDs inválidos e dados corrompidos
- **Métodos:** `save(HolderLookup.Provider)` e `load(CompoundTag, HolderLookup.Provider)`

**GETTERS E CONFIGURAÇÃO:**
- **Imutabilidade:** Getters retornam cópias para prevenir modificação externa
- **Configuração:** Métodos para alterar nome, senha, visibilidade da party
- **Queries:** Métodos de conveniência para verificar status e progresso

**🚀 CARACTERÍSTICAS AVANÇADAS:**
- **Thread-Safety:** Designed para uso em ambiente multithread de servidor
- **Configurabilidade:** Respeita todas as configurações do mod dinamicamente
- **Escalabilidade:** Otimizado para parties de até 10 membros
- **Debugging:** Extensive logging quando debug habilitado
- **Data Integrity:** Validação e proteção contra corrupção de dados

---

#### `PartyProgressionCoordinator`

**Arquivo:** `net.mirai.dimtr.data.PartyProgressionCoordinator`

Coordenador especializado para progressão de parties, extraído do `ProgressionCoordinator` monolítico como parte da refatoração arquitetural. Implementa lógica específica para coordenação de progresso em grupos, incluindo sincronização entre membros e verificação de completion de fases.

**✅ RESPONSABILIDADES PRINCIPAIS:**
- **Processamento de Mob Kills em Party:** Coordena kills compartilhados entre membros
- **Objetivos Especiais de Party:** Marca objetivos como completos para toda a party
- **Transferência de Progresso:** Bidirectional transfer entre individual e party
- **Verificação de Completion:** Detecta automaticamente quando fases são completadas
- **Acesso a Dimensões:** Controla acesso baseado no progresso da party

**🔒 THREAD-SAFETY:**
- Usa `synchronized (PROCESSING_LOCK)` para operações críticas
- Garante que modificações de progresso sejam atômicas
- Previne race conditions em ambiente multithread

**PROCESSAMENTO DE MOB KILLS:**
- **`processPartyMobKill(UUID playerId, String mobType, ServerLevel serverLevel)`**
  - Valida se jogador está em party
  - Incrementa kill compartilhado via `party.incrementSharedMobKillWithContribution()`
  - **🎆 NOVO: Sistema de Celebração:**
    - Verifica completion de fases pela primeira vez
    - Lança fogos de artifício para todos os membros usando `NotificationHelper.launchCelebrationFireworks()`
    - Phase 1: fogos tipo 1, Phase 2: fogos tipo 2
  - Sincroniza party para todos os membros

**PROCESSAMENTO DE OBJETIVOS ESPECIAIS:**
- **`processPartySpecialObjective(UUID playerId, String objectiveType, ServerLevel serverLevel)`**
  - **Switch Completo:** Processa todos os tipos de objetivos usando constantes:
    - `OBJECTIVE_TYPE_ELDER_GUARDIAN` → `setSharedElderGuardianKilled(true)`
    - `OBJECTIVE_TYPE_RAID` → `setSharedRaidWon(true)`  
    - `OBJECTIVE_TYPE_TRIAL_VAULT` → `setSharedTrialVaultAdvancementEarned(true)`
    - `OBJECTIVE_TYPE_VOLUNTARY_EXILE` → `setSharedVoluntaireExileAdvancementEarned(true)`
    - `OBJECTIVE_TYPE_WITHER` → `setSharedWitherKilled(true)`
    - `OBJECTIVE_TYPE_WARDEN` → `setSharedWardenKilled(true)`

  - **Detecção de Novidade:** Retorna `true` apenas se objetivo foi newly completed (não já completo)
  - **Verificação de Completion:** Chama `checkPhaseCompletionForParty()` se foi newly completed
  - **Warning Handling:** Log de warning para objective types desconhecidos

**VERIFICAÇÃO DE COMPLETION DE FASES:**
- **`checkPhaseCompletionForParty(PartyData party, ServerLevel serverLevel)`**
  - **Fase 1:** Verifica usando `isPhase1CompleteForParty()` e marca `phase1SharedCompleted`
  - **Fase 2:** Verifica usando `isPhase2CompleteForParty()` e marca `phase2SharedCompleted`
  - **Notificações:** Chama `notifyPartyMembers()` para todos os membros online
  - **Logging:** Info logging quando fases são completadas
  - **Uma Vez Apenas:** Só processa se a fase não estava previamente marcada como completa

**ALGORITMOS DE VERIFICAÇÃO DE FASES:**
- **`isPhase1CompleteForParty(PartyData party)`**
  - **Objetivos Especiais Obrigatórios:**
    - Elder Guardian derrotado
    - Raid vencida
    - Trial Vault: Conquista relacionada aos baús de trial chambers
    - Voluntaire Exile: Conquista relacionada a pillagers/saqueadores (se `reqVoluntaryExile` habilitado)
  - **Mob Kills (se `enableMobKillsPhase1.get()` habilitado):** Verifica todos os 16 mobs da Fase 1
  - **Aplicação de Multiplicadores:** Todos os requisitos são ajustados pelo multiplicador da party
  - **Configuração Dinâmica:** Respeita todas as flags de habilitação

- **`isPhase2CompleteForParty(PartyData party)`**
  - **Objetivos Especiais:** Wither derrotado AND Warden derrotado
  - **Mob Kills (se `enableMobKillsPhase2.get()` habilitado):**
    - TODO: Implementar verificação de mob kills da Fase 2 (placeholder presente)
  - **Pré-requisito Implícito:** Não verifica explicitamente se Fase 1 está completa (assumido)

**SISTEMA DE NOTIFICAÇÕES:**
- **`notifyPartyMembers(PartyData party, String message, ServerLevel serverLevel)`**
  - **Iteração de Membros:** Percorre todos os UUIDs em `party.getMembers()`
  - **Verificação Online:** Usa `ServerPlayer member = serverLevel.getServer().getPlayerList().getPlayer(memberId)`
  - **Envio de Mensagem:** `I18nHelper.sendMessage(member, "party.phase.complete", message)`
  - **Tolerância a Falhas:** Ignora silenciosamente membros offline

**TRANSFERÊNCIA DE PROGRESSO:**
- **`transferIndividualToParty(UUID playerId, ServerLevel serverLevel)`**
  - **Synchronization:** Protected por `PROCESSING_LOCK`
  - **Dados Sources:** Obtém `PartyData` e `PlayerProgressionData`
  - **Transferência de Objetivos:** Transfere todos os objetivos especiais se não já presentes na party
  - **Transfer Logic:** Usa lógica OR (se jogador tem, party ganha)
  - **Mob Kills Transfer:** TODO implementado (placeholders presentes)
  - **Logging:** Info logging para tracking de transferências

- **`transferPartyToIndividual(UUID playerId, ServerLevel serverLevel)`**
  - **Timing Crítico:** Deve ser chamado ANTES de remover jogador da party
  - **Busca Prévia:** Obtém dados da party antes da remoção
  - **Transferência Reversa:** Transfere progresso da party para individual
  - **Preserve Progress:** Usa lógica OR para preservar progresso máximo
  - **Status de Fases:** Também transfere `phase1Completed` e `phase2Completed`

**MÉTODOS DE CONVENIÊNCIA (Compatibility Layer):**
- `processMobKill()`: Alias para `processPartyMobKill()`
- `processSpecialObjective()`: Alias para `processPartySpecialObjective()`
- `canPlayerAccessDimension()`: Delega para `PartyData.canAccessNether()` ou `canAccessEnd()`
