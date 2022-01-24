package machines.gui.automates;

import javafx.collections.ListChangeListener;
import machines.gui.VueEtat;
import machines.gui.VueMachine;
import machines.logique.Transition;
import machines.logique.automates.Automate;
import machines.logique.automates.TransitionAtmt;

public class VueAutomate extends VueMachine {
    private Automate automate;
    private VuePrincipaleAtmt vuePrincipaleAtmt;
    private ListChangeListener<VueEtat> miseAJourVuesEtatSelectionnes =
            change -> vuePrincipaleAtmt.getHBoxAjoutTransition()
                    .setVisible(getVuesEtatSelectionnes().size() <= 2 && getVuesEtatSelectionnes().size() >= 1);

    public VueAutomate(Automate automate, VuePrincipaleAtmt vuePrincipale) {
        super(automate, vuePrincipale);
        this.automate = automate;
        this.vuePrincipaleAtmt = vuePrincipale;

        getVuesEtatSelectionnes().addListener(miseAJourVuesEtatSelectionnes);
    }

    public VuePrincipaleAtmt getVuePrincipaleAtmt() {
        return vuePrincipaleAtmt;
    }

    public Automate getAutomate() {
        return automate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ajoutVueTransition(Transition transition) {
        VueTransitionAtmt vueTransition = new VueTransitionAtmt(transition, VueAutomate.this);
        getChildren().add(vueTransition);
        vueTransition.toBack();
        int nbrTrans = 0;
        for (Transition t : transition.getEtatDepart().getListeTransitions()) {
            if (t.getEtatArrivee() == transition.getEtatArrivee()) nbrTrans++;
        }
        vueTransition.positionnerLabelEtiquette(nbrTrans - 1);
    }
}