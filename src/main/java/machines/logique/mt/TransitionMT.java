package machines.logique.mt;

import machines.logique.Etat;
import machines.logique.Transition;

public class TransitionMT extends Transition {
    private char nouvelleLettre;
    private Mouvement mouvement;

    public TransitionMT(Etat etatDepart, Etat etatArrivee, char etiquette, char nouvelleLettre, Mouvement mouvement) {
        super(etatDepart, etatArrivee, etiquette);
        this.nouvelleLettre = nouvelleLettre;
        this.mouvement = mouvement;
    }

    public char getNouvelleLettre() {
        return nouvelleLettre;
    }

    public Mouvement getMouvement() {
        return mouvement;
    }
}
