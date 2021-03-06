/*
 * see license.txt 
 */
package seventh.client.gfx.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientTeam;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;

/**
 * @author Tony
 *
 */
public class Scoreboard {

    private ClientGame game;

    private boolean showScoreBoard;
    private boolean gameEnded;
    private Map<ClientTeam, Integer> teamScores;
    private ClientTeam winningTeam;
    /**
     * 
     */
    public Scoreboard(ClientGame game) {
        this.game = game;
        this.showScoreBoard = false;
        this.teamScores = new HashMap<ClientTeam, Integer>();
        setScore(ClientTeam.ALLIES, 0);
        setScore(ClientTeam.AXIS, 0);
        setScore(ClientTeam.NONE, 0);
    }
    
    /**
     * @param gameEnded the gameEnded to set
     */
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }
    
    /**
     * @param winner the winner to set
     */
    public void setWinner(ClientTeam winner) {
        this.winningTeam = winner;
    }
    
    
    /**
     * @param showScoreBoard the showScoreBoard to set
     */
    public void showScoreBoard(boolean showScoreBoard) {
        this.showScoreBoard = showScoreBoard;
    }
    
    public boolean isVisible() {
        return this.showScoreBoard;
    }
    
    public void setScore(ClientTeam team, int score) {
        this.teamScores.put(team, score);
    }
    
    /**
     * @return the axis score
     */
    public int getAxisScore() {
        return this.teamScores.get(ClientTeam.AXIS);
    }
    
    /**
     * @return the allies score
     */
    public int getAlliedScore() {
        return this.teamScores.get(ClientTeam.ALLIES);
    }
    
    private void setSmallFont(Canvas canvas) {
        canvas.setFont("Consola", 18);
        canvas.boldFont();
    }
    
    private void setBigFont(Canvas canvas) {
        canvas.setFont("Consola", 24);
        canvas.boldFont();
    }
    
    private int drawTeam(Canvas canvas, List<ClientPlayer> team, int x, int y) {
        setSmallFont(canvas);
        for(ClientPlayer player : team) {
            y += 30;
            
            String output = String.format("%-33s %-13d %-13d %-13d", player.getName(),player.getKills(),player.getDeaths(), player.getPing());
            RenderFont.drawShadedString(canvas, output, x, y, 0xffffffff );
            if(!player.isAlive()) {
                canvas.drawScaledImage(Art.deathsImage, x-30, y-12, 20, 20, 0xffffffff);
            }
        }
        setBigFont(canvas);
        
        return y;
    }
    
    /**
     * Renders the scoreboard
     * 
     * @param canvas
     */
    public void drawScoreboard(Canvas canvas) {
        if(!isVisible()) {
            return;
        }
                
        int yIncBig = 50;
        int y = 140;
        int x = 60;    
        
        setBigFont(canvas);
        
        int defaultColor = 0xffffffff;

        RenderFont.drawShadedString(canvas, "Name                     Kills     Deaths     Ping", x, y, defaultColor);
        List<ClientPlayer> vals = game.getPlayers().asList();
        Map<ClientTeam, List<ClientPlayer>> teams = new HashMap<ClientTeam, List<ClientPlayer>>();
        teams.put(ClientTeam.NONE, new ArrayList<ClientPlayer>());
        teams.put(ClientTeam.ALLIES, new ArrayList<ClientPlayer>());
        teams.put(ClientTeam.AXIS, new ArrayList<ClientPlayer>());
        
        Collections.sort(vals, new Comparator<ClientPlayer>() {

            @Override
            public int compare(ClientPlayer a, ClientPlayer b) {
                return b.getKills() - a.getKills();
            }
        });
        
        for(ClientPlayer player : vals) {
            ClientTeam team = player.getTeam();
            if(team != null) {
                teams.get(team).add(player);
            }
            else {
                teams.get(ClientTeam.NONE).add(player);
            }
        }
        
        int numberOfBlue = teams.get(ClientTeam.ALLIES).size();
        int numberOfRed = teams.get(ClientTeam.AXIS).size();
        int numberOfIndividuals = teams.get(ClientTeam.NONE).size();
        if(numberOfIndividuals>0) {
            if(numberOfBlue>0||numberOfRed>0) {
                y+=yIncBig;
                
                RenderFont.drawShadedString(canvas, "Spectators", x, y, defaultColor);
                y = drawTeam(canvas, teams.get(ClientTeam.NONE), x, y);
            }
        }

        int redScore = this.teamScores.get(ClientTeam.AXIS);
        int blueScore = this.teamScores.get(ClientTeam.ALLIES);
        
        if(blueScore > redScore) {
        
            if (numberOfBlue>0) {
                y+=yIncBig;

                canvas.fillCircle(14, x - 38, y - 18, 0xff000000);
                canvas.fillCircle(13, x - 37, y - 17, 0xffffffff);
                canvas.drawImage(Art.alliedIcon, x - 40, y - 20, null);
                
                RenderFont.drawShadedString(canvas, "Allies " + blueScore, x, y, ClientTeam.ALLIES.getColor());
                y = drawTeam(canvas, teams.get(ClientTeam.ALLIES), x, y);
            }
            
            if (numberOfRed>0) {
                y+=yIncBig;
                
                canvas.fillCircle(13, x - 37, y - 17, 0xffffffff);
                canvas.drawImage(Art.axisIcon, x - 40, y - 20, null);
                
                RenderFont.drawShadedString(canvas, "Axis " + redScore, x, y, ClientTeam.AXIS.getColor());
                y = drawTeam(canvas, teams.get(ClientTeam.AXIS), x, y);
            }
        }
        else {

            if (numberOfRed>0) {
                y+=yIncBig;
                
                canvas.fillCircle(13, x - 37, y - 17, 0xffffffff);
                canvas.drawImage(Art.axisIcon, x - 40, y - 20, null);
                
                RenderFont.drawShadedString(canvas, "Axis " + redScore, x, y, ClientTeam.AXIS.getColor());
                y = drawTeam(canvas, teams.get(ClientTeam.AXIS), x, y);
            }
            
            if (numberOfBlue>0) {
                y+=yIncBig;
                
                canvas.fillCircle(14, x - 38, y - 18, 0xff000000);
                canvas.fillCircle(13, x - 37, y - 17, 0xffffffff);
                canvas.drawImage(Art.alliedIcon, x - 40, y - 20, null);
                
                //canvas.drawScaledImage(Art.axisIcon, x-32, y-16, 24, 24, null);
                RenderFont.drawShadedString(canvas, "Allies " + blueScore, x, y, ClientTeam.ALLIES.getColor());
                y = drawTeam(canvas, teams.get(ClientTeam.ALLIES), x, y);
                
            }
            
        }

        if(gameEnded) {
            String winner = "Allies win!";
            int color = 0xffffffff;
            
            if(winningTeam!=null&&winningTeam!=ClientTeam.NONE) {
                winner = winningTeam.getName() + " win!";
                color = winningTeam.getColor();
            }
            else {
            
                if(blueScore>redScore) {            
                    winner = "Allies win!";
                    color = ClientTeam.ALLIES.getColor();
                }
                else if(blueScore<redScore) {            
                    winner = "Axis win!";
                    color = ClientTeam.AXIS.getColor();
                }
                else {
                    winner = "It's a tie!";
                }
            }
            
            
            y+=yIncBig;
            int strWidth = canvas.getWidth(winner);
            RenderFont.drawShadedString(canvas, winner, canvas.getWidth() / 2 - strWidth/2, y, color);
        }
        
    }
}
