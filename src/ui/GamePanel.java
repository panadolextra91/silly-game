package ui;

import core.*;
import entities.enemies.Enemy;
import entities.towers.Tower;
import entities.projectiles.Projectile;
import entities.towers.*;
import utils.Vector2D;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Main game rendering panel
 */
public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    private GameState gameState;
    private int width, height;
    
    // Mouse interaction
    private Vector2D mousePosition;
    private Tower selectedTower;
    private TowerType selectedTowerType;
    private boolean placingTower;
    
    // Visual feedback
    private String statusMessage;
    private long messageEndTime;
    private final long MESSAGE_DURATION = 2000; // 2 seconds
    
    // Colors
    private final Color BACKGROUND_COLOR = new Color(34, 139, 34); // Forest green
    private final Color PATH_COLOR = new Color(139, 119, 101); // Saddle brown
    private final Color GRID_COLOR = new Color(0, 100, 0, 50); // Semi-transparent green
    
    public GamePanel(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameState = GameState.getInstance();
        this.mousePosition = new Vector2D();
        this.placingTower = false;
        this.selectedTowerType = TowerType.ARCHER;
        
        setPreferredSize(new Dimension(width, height));
        setBackground(BACKGROUND_COLOR);
        
        // Add mouse and keyboard listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background elements
        drawBackground(g2d);
        drawGrid(g2d);
        drawPath(g2d);
        
        // Draw game entities
        drawProjectiles(g2d);
        drawEnemies(g2d);
        drawTowers(g2d);
        drawHouse(g2d);
        
        // Draw UI overlays
        drawTowerPlacement(g2d);
        drawSelectedTowerInfo(g2d);
        drawStatusMessages(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draw background
     */
    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, width, height);
    }
    
    /**
     * Draw grid for tower placement
     */
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        int gridSize = 25;
        
        // Vertical lines
        for (int x = 0; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }
        
        // Horizontal lines
        for (int y = 0; y < height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }
    }
    
    /**
     * Draw enemy path
     */
    private void drawPath(Graphics2D g2d) {
        List<List<Vector2D>> paths = gameState.getEnemyPaths();
        for (List<Vector2D> path : paths) {
            if (path.size() < 2) continue;
            g2d.setColor(PATH_COLOR);
            g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < path.size() - 1; i++) {
                Vector2D start = path.get(i);
                Vector2D end = path.get(i + 1);
                g2d.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            }
            g2d.setColor(new Color(101, 67, 33));
            g2d.setStroke(new BasicStroke(2));
            for (int i = 0; i < path.size() - 1; i++) {
                Vector2D start = path.get(i);
                Vector2D end = path.get(i + 1);
                drawArrow(g2d, start, end);
            }
        }
    }
    
    /**
     * Draw arrow on path
     */
    private void drawArrow(Graphics2D g2d, Vector2D start, Vector2D end) {
        Vector2D direction = end.subtract(start).normalize();
        Vector2D center = start.add(end.subtract(start).multiply(0.5));
        
        int arrowLength = 8;
        Vector2D arrowTip = center.add(direction.multiply(arrowLength));
        Vector2D arrowLeft = center.add(direction.multiply(-arrowLength / 2))
                                  .add(new Vector2D(-direction.y, direction.x).multiply(arrowLength / 2));
        Vector2D arrowRight = center.add(direction.multiply(-arrowLength / 2))
                                   .add(new Vector2D(direction.y, -direction.x).multiply(arrowLength / 2));
        
        int[] xPoints = {(int) arrowTip.x, (int) arrowLeft.x, (int) arrowRight.x};
        int[] yPoints = {(int) arrowTip.y, (int) arrowLeft.y, (int) arrowRight.y};
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draw all enemies
     */
    private void drawEnemies(Graphics2D g2d) {
        for (Enemy enemy : gameState.getEnemies()) {
            if (enemy.isActive()) {
                enemy.render(g2d);
            }
        }
    }
    
    /**
     * Draw all towers
     */
    private void drawTowers(Graphics2D g2d) {
        for (Tower tower : gameState.getTowers()) {
            if (tower.isActive()) {
                tower.render(g2d);
            }
        }
    }
    
    /**
     * Draw all projectiles
     */
    private void drawProjectiles(Graphics2D g2d) {
        for (Projectile projectile : gameState.getProjectiles()) {
            if (projectile.isActive()) {
                projectile.render(g2d);
            }
        }
    }
    
    /**
     * Draw tower placement preview
     */
    private void drawTowerPlacement(Graphics2D g2d) {
        if (placingTower && selectedTowerType != null) {
            // Draw tower preview
            Color towerColor = getTowerColor(selectedTowerType);
            g2d.setColor(new Color(towerColor.getRed(), towerColor.getGreen(), towerColor.getBlue(), 128));
            
            int size = 20;
            int x = (int) (mousePosition.x - size / 2);
            int y = (int) (mousePosition.y - size / 2);
            g2d.fillRect(x, y, size, size);
            
            // Draw range preview
            double range = getTowerRange(selectedTowerType);
            g2d.setColor(new Color(255, 255, 255, 50));
            int diameter = (int) (range * 2);
            int rangeX = (int) (mousePosition.x - range);
            int rangeY = (int) (mousePosition.y - range);
            g2d.fillOval(rangeX, rangeY, diameter, diameter);
            
            g2d.setColor(Color.WHITE);
            g2d.drawOval(rangeX, rangeY, diameter, diameter);
            
            // Draw cost
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String costText = "Cost: " + getTowerCost(selectedTowerType);
            g2d.drawString(costText, x, y - 5);
        }
    }
    
    /**
     * Draw selected tower information
     */
    private void drawSelectedTowerInfo(Graphics2D g2d) {
        if (selectedTower != null) {
            // Draw range circle
            g2d.setColor(new Color(255, 255, 0, 50)); // Semi-transparent yellow
            double range = selectedTower.getRange();
            int diameter = (int) (range * 2);
            int x = (int) (selectedTower.getPosition().x - range);
            int y = (int) (selectedTower.getPosition().y - range);
            g2d.fillOval(x, y, diameter, diameter);
            
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, diameter, diameter);
            
            // Draw tower stats
            drawTowerStats(g2d, selectedTower);
        }
    }
    
    /**
     * Draw tower statistics
     */
    private void drawTowerStats(Graphics2D g2d, Tower tower) {
        Vector2D pos = tower.getPosition();
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int) pos.x + 30, (int) pos.y - 40, 150, 80);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        String[] stats = {
            "Level: " + tower.getLevel(),
            "Damage: " + tower.getDamage(),
            "Range: " + (int) tower.getRange(),
            "Fire Rate: " + String.format("%.1f", tower.getFireRate()),
            "Kills: " + tower.getTotalKills(),
            "Upgrade: $" + tower.getUpgradeCost()
        };
        
        for (int i = 0; i < stats.length; i++) {
            g2d.drawString(stats[i], (int) pos.x + 35, (int) pos.y - 25 + (i * 12));
        }
    }
    
    /**
     * Draw house
     */
    private void drawHouse(Graphics2D g2d) {
        House house = gameState.getHouse();
        if (house != null) {
            house.render(g2d);
        }
    }
    
    /**
     * Draw status messages
     */
    private void drawStatusMessages(Graphics2D g2d) {
        long currentTime = System.currentTimeMillis();
        
        if (statusMessage != null && currentTime < messageEndTime) {
            g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
            g2d.fillRect(10, height - 60, width - 20, 50);
            
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(statusMessage);
            int x = (width - textWidth) / 2;
            int y = height - 30;
            g2d.drawString(statusMessage, x, y);
        }
        
        // Draw game timer
        drawGameTimer(g2d);
    }
    
    /**
     * Draw game timer
     */
    private void drawGameTimer(Graphics2D g2d) {
        double timeLeft = gameState.getGameDuration() - gameState.getGameTime();
        if (timeLeft < 0) timeLeft = 0;
        
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(10, 10, 200, 30);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(String.format("Time: %.1f", timeLeft), 20, 30);
        
        // Draw timer bar
        double progress = timeLeft / gameState.getGameDuration();
        int barWidth = 180;
        int barHeight = 8;
        int barX = 20;
        int barY = 35;
        
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        g2d.setColor(Color.GREEN);
        int greenWidth = (int) (barWidth * progress);
        g2d.fillRect(barX, barY, greenWidth, barHeight);
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    /**
     * Show a temporary message
     */
    public void showMessage(String message) {
        this.statusMessage = message;
        this.messageEndTime = System.currentTimeMillis() + MESSAGE_DURATION;
    }
    
    /**
     * Show wave start message
     */
    public void showWaveStartMessage(String message) {
        showMessage(message);
    }
    
    /**
     * Show wave complete message
     */
    public void showWaveCompleteMessage(String message) {
        showMessage(message);
    }
    
    /**
     * Reset the panel
     */
    public void reset() {
        selectedTower = null;
        placingTower = false;
        statusMessage = null;
    }
    
    // Mouse event handlers
    @Override
    public void mouseClicked(MouseEvent e) {
        mousePosition.set(e.getX(), e.getY());
        System.out.println("[INPUT][MouseClicked] button=" + e.getButton() + " pos=(" + e.getX() + "," + e.getY() + ")" );
        
        if (placingTower) {
            placeTower();
        } else {
            selectTower();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition.set(e.getX(), e.getY());
        // Debug move logs can be noisy; keep minimal
    }
    
    /**
     * Place a tower at mouse position
     */
    private void placeTower() {
        System.out.println("[ACTION][PlaceTowerAttempt] type=" + selectedTowerType + " pos=" + mousePosition);
        Tower tower = createTower(selectedTowerType, mousePosition.x, mousePosition.y);
        if (tower != null) {
            if (gameState.placeTower(tower)) {
                showMessage("Tower placed!");
                System.out.println("[ACTION][PlaceTower] success id=" + tower.getId());
            } else {
                showMessage("Cannot place tower here!");
                System.out.println("[ACTION][PlaceTower] rejected");
            }
        }
        placingTower = false;
    }
    
    /**
     * Select a tower at mouse position
     */
    private void selectTower() {
        selectedTower = null;
        
        for (Tower tower : gameState.getTowers()) {
            Vector2D towerPos = tower.getPosition();
            if (mousePosition.distanceTo(towerPos) <= 15) {
                selectedTower = tower;
                System.out.println("[ACTION][SelectTower] id=" + tower.getId());
                break;
            }
        }
    }
    
    // Key event handlers
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("[INPUT][KeyPressed] code=" + e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                // Forward pause toggle via the UI panel if available
                SwingUtilities.getWindowAncestor(this);
                if (SwingUtilities.getWindowAncestor(this) instanceof GameWindow) {
                    GameWindow gw = (GameWindow) SwingUtilities.getWindowAncestor(this);
                    gw.togglePause();
                }
                break;
            case KeyEvent.VK_1:
                selectedTowerType = TowerType.ARCHER;
                placingTower = true;
                System.out.println("[ACTION][SelectTowerType] ARCHER");
                break;
            case KeyEvent.VK_2:
                selectedTowerType = TowerType.CANNON;
                placingTower = true;
                System.out.println("[ACTION][SelectTowerType] CANNON");
                break;
            case KeyEvent.VK_3:
                selectedTowerType = TowerType.LIGHTNING;
                placingTower = true;
                System.out.println("[ACTION][SelectTowerType] LIGHTNING");
                break;
            case KeyEvent.VK_4:
                selectedTowerType = TowerType.ICE;
                placingTower = true;
                System.out.println("[ACTION][SelectTowerType] ICE");
                break;
            case KeyEvent.VK_5:
                selectedTowerType = TowerType.POISON;
                placingTower = true;
                System.out.println("[ACTION][SelectTowerType] POISON");
                break;
            case KeyEvent.VK_U:
                if (selectedTower != null) {
                    System.out.println("[ACTION][UpgradeTower] id=" + selectedTower.getId());
                    upgradeTower();
                }
                break;
            case KeyEvent.VK_S:
                if (selectedTower != null) {
                    System.out.println("[ACTION][SellTower] id=" + selectedTower.getId());
                    sellTower();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                placingTower = false;
                selectedTower = null;
                System.out.println("[ACTION][CancelPlacement]");
                break;
        }
    }
    
    /**
     * Upgrade selected tower
     */
    private void upgradeTower() {
        if (selectedTower.canUpgrade()) {
            if (gameState.getPlayerMoney() >= selectedTower.getUpgradeCost()) {
                gameState.subtractMoney(selectedTower.getUpgradeCost());
                selectedTower.upgrade();
                showMessage("Tower upgraded!");
            } else {
                showMessage("Not enough money!");
            }
        } else {
            showMessage("Tower at max level!");
        }
    }
    
    /**
     * Sell selected tower
     */
    private void sellTower() {
        int sellValue = selectedTower.getSellValue();
        gameState.addMoney(sellValue);
        gameState.getTowers().remove(selectedTower);
        selectedTower.destroy();
        selectedTower = null;
        showMessage("Tower sold for $" + sellValue);
    }
    
    // Helper methods
    private Tower createTower(TowerType type, double x, double y) {
        switch (type) {
            case ARCHER: return new ArcherTower(x, y);
            case CANNON: return new CannonTower(x, y);
            case LIGHTNING: return new LightningTower(x, y);
            case ICE: return new IceTower(x, y);
            case POISON: return new PoisonTower(x, y);
            default: return null;
        }
    }
    
    private Color getTowerColor(TowerType type) {
        switch (type) {
            case ARCHER: return new Color(139, 69, 19);
            case CANNON: return Color.DARK_GRAY;
            case LIGHTNING: return Color.YELLOW;
            case ICE: return new Color(173, 216, 230);
            case POISON: return new Color(128, 255, 0);
            default: return Color.BLACK;
        }
    }
    
    private double getTowerRange(TowerType type) {
        switch (type) {
            case ARCHER: return 80.0;
            case CANNON: return 70.0;
            case LIGHTNING: return 90.0;
            case ICE: return 75.0;
            case POISON: return 65.0;
            default: return 75.0;
        }
    }
    
    private int getTowerCost(TowerType type) {
        switch (type) {
            case ARCHER: return 50;
            case CANNON: return 120;
            case LIGHTNING: return 85;
            case ICE: return 70;
            case POISON: return 90;
            default: return 50;
        }
    }
    
    // Unused mouse event methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}

/**
 * Tower types enum
 */
enum TowerType {
    ARCHER, CANNON, LIGHTNING, ICE, POISON
}
