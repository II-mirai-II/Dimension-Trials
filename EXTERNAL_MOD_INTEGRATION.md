# Integração Automática com Mods Externos - Dimension Trials

## 🎯 Visão Geral

O sistema de integração automática permite que o mod Dimension Trials detecte e integre automaticamente bosses de mods compatíveis como objetivos obrigatórios nas fases apropriadas.

### Mods Suportados
- **Mowzie's Mobs** (`mowziesmobs`)
- **L_Ender's Cataclysm** (`cataclysm`)

## 🔧 Configuração

### Configurações Principais (Server)

No arquivo `config/dimtr-server.toml`:

```toml
[external_mod_integration]
    # Habilitar integração automática com mods externos
    enableExternalModIntegration = true
    
    # Habilitar integração específica com Mowzie's Mobs
    enableMowziesModsIntegration = true
    
    # Habilitar integração específica com L_Ender's Cataclysm
    enableCataclysmIntegration = true
    
    # Tornar bosses de mods externos obrigatórios para progressão
    requireExternalModBosses = true
    
    # Criar Fase 3 para bosses do End de mods externos
    createPhase3ForEndBosses = true
```

## 🎮 Como Funciona

### Detecção Automática

1. **Durante a inicialização do servidor**, o sistema:
   - Verifica se os mods estão presentes
   - Confirma que as entidades dos bosses existem
   - Cria automaticamente arquivos de configuração customizada

2. **Arquivos Gerados**:
   - `config/dimtr/custom_requirements/mowziesmobs_integration.json`
   - `config/dimtr/custom_requirements/cataclysm_integration.json`

### Classificação por Fases

#### Mowzie's Mobs
- **Fase 1 (Overworld)**:
  - Ferrous Wroughtnaut
  - Frostmaw
  - Barako the Sun Chief
- **Fase 2 (Nether-tier)**:
  - Umvuthi the Sunbird

#### L_Ender's Cataclysm
- **Fase 1 (Overworld)**:
  - Netherite Monstrosity
  - Ignis
- **Fase 2 (Nether-tier)**:
  - The Harbinger
- **Fase 3 (End-tier)**:
  - Ender Guardian
  - Ancient Remnant
  - The Leviathan

## 📋 Sistema de Objetivos

### Objetivos Especiais Criados

Cada boss se torna um **Special Objective** obrigatório (se `requireExternalModBosses = true`):

```json
{
  "specialObjectives": {
    "ferrous_wroughtnaut": {
      "displayName": "Ferrous Wroughtnaut Defeated",
      "description": "Defeat the armored construct in the deep caves",
      "required": true
    }
  }
}
```

### Progresso Individual e Party

- **Sistema Individual**: Cada jogador deve derrotar o boss uma vez
- **Sistema Party**: A party compartilha o objetivo - apenas um membro precisa derrotar o boss
- **Sincronização**: Progresso é sincronizado entre todos os membros da party

## 🎯 Exemplos de Uso

### Cenário 1: Só Mowzie's Mobs Instalado

```
Fase 1 - Overworld (Vanilla + Mowzie's)
├── Mobs Vanilla: 15 Zombies, 10 Skeletons, etc.
├── Special Objectives:
│   ├── Elder Guardian ✓
│   ├── Raid Won ✓
│   ├── Ferrous Wroughtnaut ✓ (NOVO)
│   ├── Frostmaw ✓ (NOVO)
│   └── Barako the Sun Chief ✓ (NOVO)
└── Acesso: Nether

Fase 2 - Nether (Vanilla + Mowzie's)
├── Mobs Vanilla: 20 Blazes, 15 Wither Skeletons, etc.
├── Special Objectives:
│   ├── Wither ✓
│   ├── Warden ✓
│   └── Umvuthi the Sunbird ✓ (NOVO)
└── Acesso: End
```

### Cenário 2: Ambos Mods Instalados

```
Fase 1 - Overworld
├── Bosses: Ferrous Wroughtnaut, Frostmaw, Barako, Netherite Monstrosity, Ignis
└── Acesso: Nether

Fase 2 - Nether-tier
├── Bosses: Umvuthi, The Harbinger
└── Acesso: End

Fase 3 - End-tier (NOVA FASE)
├── Bosses: Ender Guardian, Ancient Remnant, The Leviathan
└── Acesso: Dimensões Customizadas
```

## ⚙️ Configuração Flexível

### Desabilitar Obrigatoriedade

```toml
# Tornar bosses opcionais em vez de obrigatórios
requireExternalModBosses = false
```

### Desabilitar Fase 3

```toml
# Mover bosses do End para a Fase 2
createPhase3ForEndBosses = false
```

### Desabilitar Mod Específico

```toml
# Desabilitar apenas Mowzie's Mobs
enableMowziesModsIntegration = false
```

## 🔄 Comandos Administrativos

### Verificar Status da Integração

```
/dimtr integration status
```

### Recarregar Integração

```
/dimtr integration reload
```

### Ver Bosses Detectados

```
/dimtr integration list
```

## 📝 Logs do Sistema

### Inicialização Bem-Sucedida

```
[INFO] 🔍 Iniciando detecção de mods externos para integração...
[INFO] 📋 Registrados 4 bosses do Mowzie's Mobs e 6 bosses do L_Ender's Cataclysm
[INFO] ✅ Mod detectado e verificado: mowziesmobs
[INFO] ✅ Mod detectado e verificado: cataclysm
[INFO] 💾 Arquivo de integração criado para Mowzie's Mobs: mowziesmobs_integration.json
[INFO] 💾 Arquivo de integração criado para L_Ender's Cataclysm: cataclysm_integration.json
[INFO] ✅ Sistema de integração com mods externos inicializado!
[INFO] 🎯 Mods detectados: [mowziesmobs, cataclysm]
```

### Morte de Boss

```
[INFO] 🏆 Boss de mod externo derrotado: Ferrous Wroughtnaut por jogador abc123 (Fase: mowziesmobs_phase1)
[INFO] ✅ Individual custom objective completed: mowziesmobs_phase1 - ferrous_wroughtnaut by abc123
```

## 🛠️ Troubleshooting

### Mods Não Detectados

1. **Verificar se o mod está instalado e funcionando**
2. **Verificar logs para mensagens de erro**
3. **Confirmar que as entidades dos bosses existem**

### Bosses Não Sendo Registrados

1. **Verificar configurações de integração**
2. **Recarregar a integração com comando admin**
3. **Verificar arquivos de custom requirements gerados**

### Progresso Não Sincronizando

1. **Verificar se o jogador está em uma party**
2. **Confirmar que a progressão está habilitada**
3. **Verificar logs de sincronização**

## 🔮 Funcionalidades Avançadas

### Custom Requirements Manuais

Você pode criar arquivos de custom requirements manuais para maior controle:

```json
{
  "name": "Custom Mowzie's Integration",
  "description": "Configuração personalizada para Mowzie's Mobs",
  "enabled": true,
  "customPhases": {
    "custom_mowzies_phase1": {
      "name": "Mowzie's Overworld Challenges",
      "specialObjectives": {
        "ferrous_wroughtnaut": {
          "displayName": "Iron Golem Slayer",
          "description": "Custom description for Wroughtnaut",
          "required": false
        }
      }
    }
  }
}
```

### Multiplicadores Customizados

O sistema aplica multiplicadores progressivos:
- **Fase 1**: 1.0x (base)
- **Fase 2**: 1.5x
- **Fase 3**: 2.0x

## 📚 Recursos Adicionais

- **Configuração**: [CONFIGURATION.md](CONFIGURATION.md)
- **Custom Requirements**: [CUSTOM_REQUIREMENTS.md](CUSTOM_REQUIREMENTS.md)
- **Sistema de Party**: [PARTY_SYSTEM.md](PARTY_SYSTEM.md)
- **README Principal**: [README.md](README.md)
