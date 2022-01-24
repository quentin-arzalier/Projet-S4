package machines.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import machines.logique.Etat;
import machines.logique.Machine;
import machines.logique.Transition;

import java.io.*;
import java.util.ArrayList;

public abstract class VueMachine extends Pane {
    private Machine machine;
    private VuePrincipale vuePrincipale;
    private ObservableList<VueEtat> vuesEtatSelectionnes = FXCollections.observableArrayList();
    private ObservableList<VueTransition> vuesTransitionSelectionnes = FXCollections.observableArrayList();
    private SetChangeListener<Etat> miseAJourEtats = change -> {
        if (change.wasAdded()) {
            VueEtat vueEtat = new VueEtat(change.getElementAdded(), VueMachine.this);
            vueEtat.setLabelNumEtat(machine.getEtats().size() - 1);
            getChildren().add(vueEtat);
        } else if (change.wasRemoved()) {
            VueEtat vueEtatRemoved = getVueEtat(change.getElementRemoved());
            getChildren().remove(vueEtatRemoved);
            for (Node n : getChildren()) {
                if (n instanceof VueEtat) {
                    VueEtat vueEtat = (VueEtat) n;
                    if (vueEtat.getNumEtat() > vueEtatRemoved.getNumEtat()) {
                        vueEtat.setLabelNumEtat((vueEtat.getNumEtat() - 1));
                    }
                }
            }

        }
    };
    private ListChangeListener<VueEtat> miseAJourVuesEtatSelectionnes = change -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for (VueEtat vueEtat : change.getAddedSubList()) {
                    vueEtat.getCercle().setStroke(Color.valueOf("#003576"));
                    vueEtat.getCercle().setStrokeType(StrokeType.INSIDE);
                    vueEtat.getCercle().setStrokeWidth(3);
                }
            }
            if (change.wasRemoved()) {
                for (VueEtat vueEtat : change.getRemoved()) {
                    vueEtat.getCercle().setStroke(null);
                }
            }
        }
    };
    private ListChangeListener<VueTransition> miseAJourVuesTransitionSelectionnes = change -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for (VueTransition vueTransition : change.getAddedSubList()) {
                    vueTransition.setCouleurSelection(true);
                }
            }
            if (change.wasRemoved()) {
                for (VueTransition vueTransition : change.getRemoved()) {
                    vueTransition.setCouleurSelection(false);
                }
            }
        }
    };

    public VueMachine(Machine machine, VuePrincipale vuePrincipale) {
        this.vuePrincipale = vuePrincipale;
        this.machine = machine;

        minWidthProperty().bind(vuePrincipale.getScrollPaneCenter().widthProperty());
        minHeightProperty().bind(vuePrincipale.getScrollPaneCenter().heightProperty());

        initListeners();

        setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getTarget() == this) {
                deSelectionnerVues();
                vuePrincipale.unbindCheckBoxes();
            }
        });
    }

    private void initListeners() {
        machine.etatsProperty().addListener(miseAJourEtats);
        vuesEtatSelectionnes.addListener(miseAJourVuesEtatSelectionnes);
        vuesTransitionSelectionnes.addListener(miseAJourVuesTransitionSelectionnes);
    }

    /**
     * Ajoute une vueTransition a la vue machine
     *
     * @param transition transition de la vueTransition a ajouter
     */
    public abstract void ajoutVueTransition(Transition transition);

    /**
     * Supprime une vue transition a la vue machine
     *
     * @param transition transition de la vueTransition a supprimer
     */
    public void supprimerVueTransition(Transition transition) {
        VueTransition vueTransition = getVueTransition(transition);
        if (vueTransition != null) {
            getChildren().remove(vueTransition);
            ArrayList<VueTransition> vueTransitions =
                    getVuesTransition(vueTransition.getVueEtatDep(), vueTransition.getVueEtatFin());
            for (int i = 0; i < vueTransitions.size(); i++) {
                vueTransitions.get(i).positionnerLabelEtiquette(i);
            }
        }
    }

    public Machine getMachine() {
        return machine;
    }

    public ObservableList<VueTransition> getVuesTransitionSelectionnes() {
        return vuesTransitionSelectionnes;
    }

    public ObservableList<VueEtat> getVuesEtatSelectionnes() {
        return vuesEtatSelectionnes;
    }

    public VuePrincipale getVuePrincipale() {
        return vuePrincipale;
    }

    /**
     * Permet d'obtenir la vueEtat correspondante a l'etat
     *
     * @param etat etat de la vueEtat
     * @return vueEtat correspondante
     */
    public VueEtat getVueEtat(Etat etat) {
        for (Node n : getChildren()) {
            if (n instanceof VueEtat) {
                VueEtat vueEtat = (VueEtat) n;
                if (vueEtat.getEtat().equals(etat)) return vueEtat;
            }
        }
        return null;
    }

    /**
     * Permet d'obtenir la vueTransition correspondante a la transition
     *
     * @param transition transition de la vueTransition
     * @return vueTransition correspondante
     */
    public VueTransition getVueTransition(Transition transition) {
        for (Node n : getChildren()) {
            if (n instanceof VueTransition) {
                VueTransition vueTransition = (VueTransition) n;
                if (vueTransition.getTransition().equals(transition)) return vueTransition;
            }
        }
        return null;
    }

    /**
     * Peremet d'obtenir la liste de vueTransitions ayant les etats en parametre comme borne
     *
     * @param vueEtat1 etat de de depart
     * @param vueEtat2 etat d'arrivee
     * @return liste des vueTransition ou null si il n'y en a pas
     */
    public ArrayList<VueTransition> getVuesTransition(VueEtat vueEtat1, VueEtat vueEtat2) {
        ArrayList<VueTransition> res = new ArrayList<>();
        for (Transition t : vueEtat1.getEtat().getListeTransitions()) {
            if (t.getEtatArrivee() == vueEtat2.getEtat()) {
                if (!res.contains(getVueTransition(t))) res.add(getVueTransition(t));
            }
        }
        for (Transition t : vueEtat2.getEtat().getListeTransitions()) {
            if (t.getEtatArrivee() == vueEtat1.getEtat()) {
                if (!res.contains(getVueTransition(t))) res.add(getVueTransition(t));
            }
        }
        return res;
    }

    /**
     * Supprime tous les element de la vueMachine
     */
    public void clear() {
        vuesEtatSelectionnes.clear();
        vuesTransitionSelectionnes.clear();
        machine.clear();
    }

    /**
     * Permet de creer une machine a partir d'un fichier
     *
     * @param nomFichier nom du fichier contenant la machine
     * @throws IOException
     */
    public void chargerFichier(String nomFichier) throws IOException {
        clear();
        machine.chargerFichier(nomFichier);

        FileReader fr = new FileReader(nomFichier);
        BufferedReader bf = new BufferedReader(fr);

        String ligne = bf.readLine();

        while (!(ligne == null || ligne.contains("###"))) {
            ligne = bf.readLine();
        }

        ligne = bf.readLine();

        ArrayList<Etat> etats = new ArrayList<>(machine.getEtats());

        while (ligne != null) {
            String[] split = ligne.split(" ");

            if (split.length >= 3) {

                int numEtat = Integer.parseInt(split[0]);
                double xPos = Double.parseDouble(split[1]);
                double yPos = Double.parseDouble(split[2]);
                int labelNumEtat = Integer.parseInt(split[3]);

                Etat etat = etats.get(numEtat);
                VueEtat vueEtat = getVueEtat(etat);

                if (vueEtat != null) {
                    //Permet de faire que les coordonnées de la vue etat soient positives
                    double taille = vueEtat.getCercle().getRadius() * 2 + 20;
                    if (xPos >= 0) {
                        vueEtat.setLayoutX(xPos);
                    }
                    if (yPos >= 0) {
                        vueEtat.setLayoutY(yPos);
                    }

                    vueEtat.setLabelNumEtat(labelNumEtat);
                }
            }
            ligne = bf.readLine();
        }


        bf.close();
        fr.close();
    }

    /**
     * Perme de sauvegarder la machine dans un fichier
     *
     * @param nomFichier nom du fichier dans lequel sauvegarder la machine
     * @throws IOException
     */
    public void sauvegarder(String nomFichier) throws IOException {
        machine.sauvegarder(nomFichier);

        Writer fileWriter = new FileWriter(nomFichier, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write("###");
        bufferedWriter.newLine();

        ArrayList<Etat> etats = new ArrayList<>(machine.getEtats());

        for (Etat e : etats) {
            VueEtat vueEtat = getVueEtat(e);
            if (vueEtat != null) {
                bufferedWriter.write(etats.indexOf(e) + " " + vueEtat.getLayoutX() + " " +
                        vueEtat.getLayoutY() + " " + vueEtat.getLabelNumEtat().getText());
                bufferedWriter.newLine();
            }
        }

        bufferedWriter.close();
        fileWriter.close();
    }

    /**
     * Deselectionne toutes les vues etat et les vue transitions
     */
    public void deSelectionnerVues() {
        deSelectionnerVuesEtat();
        deSelectionnerVuesTransition();
    }

    /**
     * Deselectionne toutes les vues etats
     */
    public void deSelectionnerVuesEtat() {
        for (Etat e : machine.getEtats()) {
            VueEtat vueEtat = getVueEtat(e);
            if (vueEtat != null) vueEtat.deSelectionner();
        }
    }

    /**
     * Deselectionne toutes les vue transitions
     */
    public void deSelectionnerVuesTransition() {
        for (Transition t : machine.getTransitions()) {
            VueTransition vueTransition = getVueTransition(t);
            if (vueTransition != null) vueTransition.deSelectionner();
        }
    }
}