# Advanced Tower Defense Game

A sophisticated tower defense game built in Java using object-oriented programming principles for an OOP class project.

## Features

### 🏗️ **Advanced OOP Architecture**
- **Design Patterns**: Strategy, Factory, Observer, Singleton, Template Method
- **SOLID Principles**: Comprehensive implementation of all SOLID principles
- **Event-Driven Architecture**: Decoupled communication between game systems
- **Complex Inheritance Hierarchies**: Well-structured class relationships

### 🎮 **Game Mechanics**
- **5 Tower Types**: Each with unique abilities and upgrade paths
  - **Archer Tower**: Balanced damage and range
  - **Cannon Tower**: High damage with splash effect
  - **Lightning Tower**: Chain lightning between enemies
  - **Ice Tower**: Slows and freezes enemies
  - **Poison Tower**: Damage over time effects

- **8 Enemy Types**: Each with unique resistances and abilities
  - Basic, Fast, Armored, Flying enemies
  - Fire/Ice Elementals with immunities
  - Regenerating enemies that heal over time
  - Boss enemies every 10 waves

### 🎯 **Advanced Features**
- **Smart AI**: A* pathfinding and dynamic enemy behavior
- **Tower Targeting**: Multiple targeting strategies (First, Last, Strongest, Weakest, Closest)
- **Upgrade System**: Multi-level tower upgrades with branching paths
- **Wave Management**: Progressive difficulty with boss encounters
- **Status Effects**: Freeze, poison, and other tactical elements

## Controls

### Mouse
- **Click**: Place towers or select existing towers
- **Move**: Preview tower placement and range

### Keyboard
- **1-5**: Select tower types (Archer, Cannon, Lightning, Ice, Poison)
- **U**: Upgrade selected tower
- **S**: Sell selected tower
- **ESC**: Cancel current action

## Technical Implementation

### Core Systems
- **Game State Management**: Singleton pattern for global state
- **Event System**: Observer pattern for decoupled communication
- **Factory Pattern**: Dynamic enemy and projectile creation
- **Strategy Pattern**: Configurable tower targeting behaviors

### Performance Features
- **Object Pooling**: Efficient memory management for projectiles
- **Spatial Partitioning**: Optimized collision detection
- **Multithreading**: Separate threads for AI and rendering

## Getting Started

### Prerequisites
- Java 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)

### Running the Game
1. Clone the repository
2. Navigate to the `src` directory
3. Compile: `javac Main.java`
4. Run: `java Main`

## Project Structure
```
src/
├── core/                   # Core game systems
│   ├── Game.java          # Main game coordinator
│   ├── GameState.java     # Global state management
│   ├── GameObject.java    # Base class for all entities
│   └── GameEventManager.java # Event system
├── entities/              # Game entities
│   ├── enemies/           # Enemy types and AI
│   ├── towers/            # Tower types and behaviors
│   └── projectiles/       # Projectile system
├── patterns/              # Design pattern implementations
│   ├── strategies/        # Strategy pattern for targeting
│   └── factories/         # Factory pattern for creation
├── ui/                    # User interface
│   ├── GameWindow.java    # Main window
│   ├── GamePanel.java     # Game rendering
│   └── UIPanel.java       # Control panel
└── utils/                 # Utility classes
    └── Vector2D.java      # 2D vector mathematics
```

## Learning Objectives Achieved

This project demonstrates:
- **Object-Oriented Design**: Proper use of inheritance, polymorphism, and encapsulation
- **Design Patterns**: Real-world application of multiple design patterns
- **Software Architecture**: Modular, maintainable code structure
- **Game Development**: Complete game loop and state management
- **Performance Optimization**: Efficient algorithms and data structures

## Screenshots

The game features a clean, colorful interface with:
- Towers represented as colored rectangles
- Enemies as colored circles with health bars
- Real-time wave progression
- Interactive tower placement and upgrade system

## Future Enhancements

- Sound effects and background music
- Particle effects and animations
- Additional tower and enemy types
- Level editor for custom maps
- Multiplayer support

---

**Built with ❤️ for OOP learning and Java mastery!**
