package machines.logique.automates;

import machines.logique.Etat;
import machines.logique.Transition;
import machines.logique.mt.Mouvement;

public class TransitionAtmt extends Transition {
    public TransitionAtmt(Etat etatDepart, Etat etatArrivee, char etiquette) {
        super(etatDepart, etatArrivee, etiquette);
    }

    @Override
    public char getNouvelleLettre() {
        return 0;
    }

    @Override
    public Mouvement getMouvement() {
        return null;
    }
}
