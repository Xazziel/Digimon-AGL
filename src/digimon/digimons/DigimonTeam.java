package digimon.digimons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DigimonTeam {
    public static final int MAX_TEAM_SIZE = 6;

    private final List<Digimon> digimons = new ArrayList<>();
    private int activeIndex = 0;

    public boolean addDigimon(Digimon digimon) {
        if (digimon == null || digimons.size() >= MAX_TEAM_SIZE) {
            return false;
        }
        digimons.add(digimon);
        return true;
    }

    public Digimon getActiveDigimon() {
        if (digimons.isEmpty()) {
            return null;
        }
        if (activeIndex < 0 || activeIndex >= digimons.size()) {
            activeIndex = 0;
        }
        return digimons.get(activeIndex);
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        if (activeIndex >= 0 && activeIndex < digimons.size()) {
            this.activeIndex = activeIndex;
        }
    }

    public int size() {
        return digimons.size();
    }

    public boolean switchTo(int index) {
        if (index < 0 || index >= digimons.size()) {
            return false;
        }
        Digimon digimon = digimons.get(index);
        if (digimon.estaDebilitado()) {
            return false;
        }
        activeIndex = index;
        return true;
    }

    public boolean switchToNextAvailable() {
        for (int i = 0; i < digimons.size(); i++) {
            if (!digimons.get(i).estaDebilitado()) {
                activeIndex = i;
                return true;
            }
        }
        return false;
    }

    public boolean hasAvailableDigimon() {
        for (Digimon digimon : digimons) {
            if (!digimon.estaDebilitado()) {
                return true;
            }
        }
        return false;
    }

    public void restoreAll() {
        for (Digimon digimon : digimons) {
            digimon.restaurar();
        }
        activeIndex = 0;
    }

    public List<Digimon> getDigimons() {
        return Collections.unmodifiableList(digimons);
    }

    public String getTeamStatusText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < digimons.size(); i++) {
            Digimon digimon = digimons.get(i);
            if (i > 0) {
                text.append(" | ");
            }
            if (i == activeIndex) {
                text.append(">");
            }
            text.append(i + 1)
                    .append(".")
                    .append(digimon.getNombre())
                    .append(" Lv")
                    .append(digimon.getNivel())
                    .append(" ")
                    .append(Math.max(0, digimon.getVida()))
                    .append("/")
                    .append(digimon.getVidaMaxima());
        }
        return text.toString();
    }

    public String getTeamStatusMultiline() {
        StringBuilder text = new StringBuilder("EQUIPO DIGITAL\n");
        for (int i = 0; i < digimons.size(); i++) {
            Digimon digimon = digimons.get(i);
            text.append(i == activeIndex ? "> " : "  ")
                    .append(i + 1)
                    .append(". ")
                    .append(digimon.getNombre())
                    .append("  ")
                    .append(digimon.getEstadoCorto());
            if (digimon.estaDebilitado()) {
                text.append("  KO");
            }
            if (i < digimons.size() - 1) {
                text.append("\n");
            }
        }
        return text.toString();
    }

    public static DigimonTeam createDefaultTeam() {
        DigimonTeam team = new DigimonTeam();
        for (Digimon digimon : DigimonList.createDefaultTeamDigimon()) {
            team.addDigimon(digimon);
        }
        return team;
    }
}
