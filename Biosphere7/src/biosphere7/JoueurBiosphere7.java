package biosphere7;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Joueur implémentant les actions possibles à partir d'un plateau, pour un
 * niveau donné.
 */
public class JoueurBiosphere7 implements IJoueurBiosphere7 {

    /**
     * Nombre maximal d'actions possibles, tous niveaux confondus.
     */
    final static int MAX_NB_ACTIONS = 35285;

    /**
     * Compte le nombre d'actions possibles déjà entrées dans le tableau des
     * actions possibles.
     */
    int nbActions;
    static final int vitaliteMax = 9;
    static final int nbVoisinsMax = 4;
    int vr, vb;
    static Plante[] plantesPossibles;
    static TypePlante[] typesPlantes;

    /**
     * Cette méthode renvoie, pour un plateau donné et un joueur donné, toutes
     * les actions possibles pour ce joueur.
     *
     * @param plateau le plateau considéré
     * @param couleurJoueur couleur du joueur
     * @param niveau le niveau de la partie à jouer
     * @return l'ensemble des actions possibles
     */
    @Override
    public String[] actionsPossibles(Case[][] plateau, char couleurJoueur, int niveau) {
        // afficher l'heure de lancement
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("actionsPossibles : lancement le "
                + format.format(new Date()));
        // calculer les actions possibles
        String actions[] = new String[MAX_NB_ACTIONS];
        nbActions = 0;
        plantesPossibles = Plante.lesPlantes();
        Vitalite v = new Vitalite(0, 0);
        v.compterVitalite(plateau);
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                Coordonnees coord = new Coordonnees(lig, col);
                if (estCaseVide(plateau, coord) && symbioseVitalite(coord, plateau) < 4) {
                    ajoutActionPlanter(coord, actions, plateau, v, couleurJoueur);
                }
                if (!estCaseVide(plateau, coord) && estEspecePlante(coord, plateau)) {
                    ajoutActionCouper(coord, actions, v, plateau);
                }
                if (!estCaseVide(plateau, coord)) {
                    ajoutActionFertiliser(coord, actions, plateau, v);
                    ajoutActionDisseminer(coord, actions, plateau, v, couleurJoueur);
                }
            }
        }
        
        System.out.println("actionsPossibles : fin");
        
        return Utils.nettoyerTableau(actions);
    }


    /**
     * ajouter une action de fertilisation
     *
     * @param coord la coordonnée de la case à fertiliser
     * @param actions tableau des actions à retourner
     * @param plateau le plateau de jeu
     * @param v les vitalités
     */
    public void ajoutActionFertiliser(Coordonnees coord, String[] actions, Case[][] plateau, Vitalite v) {
        vr = v.getVitaliteR();
        vb = v.getVitaliteB();
        for (int i = 0; i < plantesPossibles.length; i++) {
            if (plateau[coord.ligne][coord.colonne].espece == plantesPossibles[i].getSymbole()) {
                switch (plateau[coord.ligne][coord.colonne].couleur) {
                    case Utils.CAR_ROUGE:
                        vr = ajouterLesFertilisations(vr, coord, plateau, i);
                        break;
                    case Utils.CAR_BLEU:
                        vb = ajouterLesFertilisations(vb, coord, plateau, i);
                        break;
                }
            }
        }
        String action = "F" + coord.carLigne() + coord.carColonne() + "," + vr + "," + vb;
        actions[nbActions] = action;
        nbActions++;
    }

    /**
     * ajouter les fertilisations aux vitalités
     *
     * @param vitalite les vitalités
     * @param coord la coordonnée de la case à fertiliser
     * @param plateau plateau de jeu
     * @param pos position de la plante dans le tableau
     * @return la nouvelle vitalité du joueur courant
     */
     public int ajouterLesFertilisations(int vitalite, Coordonnees coord, Case[][] plateau, int pos) {
        if (plateau[coord.ligne][coord.colonne].vitalite + plantesPossibles[pos].getMonType().getBonusFertilisation() < vitaliteMax) {
            vitalite += plantesPossibles[pos].getMonType().getBonusFertilisation();
        } else {
            vitalite += (vitaliteMax - plateau[coord.ligne][coord.colonne].vitalite);
        }
        return vitalite;
    }

    /**
     * ajouter une action de plantation
     *
     * @param coord la coordonnée de la case à planter
     * @param actions tableau des actions à retourner
     * @param plateau plateau de jeu
     * @param v les vitalité
     * @param couleurJoueur couleur du joueur courant
     */
    void ajoutActionPlanter(Coordonnees coord, String[] actions, Case[][] plateau, Vitalite v, char couleurJoueur) {
        vr = v.getVitaliteR();
        vb = v.getVitaliteB();
        if (couleurJoueur == Utils.CAR_ROUGE && symbioseVitalite(coord, plateau) < nbVoisinsMax) {
            vr += (1 + symbioseVitaliteMemeCouleur(coord, plateau, couleurJoueur));
        } else if (couleurJoueur == Utils.CAR_BLEU && symbioseVitalite(coord, plateau) < nbVoisinsMax) {
            vb += (1 + symbioseVitaliteMemeCouleur(coord, plateau, couleurJoueur));
        }
        verifierEtouffement(plateau, coord);
        for (int i = 0; i < plantesPossibles.length; i++) {
            String action = plantesPossibles[i].getSymboleS() + coord.carLigne() + coord.carColonne() + "," + vr + "," + vb;
            actions[nbActions] = action;
            nbActions++;
        }
    }

    /**
     * ajouter une action couper
     *
     * @param coord la coordonnée de la case à couper
     * @param actions le tableau des actions à retourner
     * @param v les vitalités
     * @param plateau plateau de jeu
     */
    void ajoutActionCouper(Coordonnees coord, String[] actions, Vitalite v, Case[][] plateau) {
        vr = v.getVitaliteR();
        vb = v.getVitaliteB();
        for (Coordonnees c : lesQuatresVoisins(coord)) {
            ajouterVitaliteCaseVoisineMmCouleur(plateau, c);
        }
        if (plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_ROUGE) {
            vr -= plateau[coord.ligne][coord.colonne].vitalite;
        } else if (plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_BLEU) {
            vb -= plateau[coord.ligne][coord.colonne].vitalite;
        }
        String action = "C" + coord.carLigne() + coord.carColonne() + "," + vr + "," + vb;
        actions[nbActions] = action;
        nbActions++;
    }

    /**
     * ajouter les vitalités en fonction de de la couleur des cases voisines
     *
     * @param plateau plateau de jeu
     * @param coord coordonnée à tester
     */
    void ajouterVitaliteCaseVoisineMmCouleur(Case[][] plateau, Coordonnees coord) {
        if (coord.estDansPlateau()) {
            if (estEspecePlante(coord, plateau)
                    && plateau[coord.ligne][coord.colonne].vitalite < vitaliteMax) {
                if (plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_ROUGE) {
                    vr++;
                } else if (plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_BLEU) {
                    vb++;
                }
            }
        }
    }

    /**
     * regarder si on n'est pas etouffé lors de la plantation
     *
     * @param plateau plateau de jeu
     * @param c coordonnée à tester
     */
    private void verifierEtouffement(Case[][] plateau, Coordonnees c) {
        appliquerEtouffer(lesQuatresVoisins(c)[0], plateau);
        appliquerEtouffer(lesQuatresVoisins(c)[1], plateau);
        appliquerEtouffer(lesQuatresVoisins(c)[2], plateau);
        appliquerEtouffer(lesQuatresVoisins(c)[3], plateau);
    }

    /**
     * enlever les vitalité d'une cases aux vitalité
     *
     * @param coord coordonné à utiliser pour la vitalité
     * @param plateau plateau de jeu
     */
    public void appliquerEtouffer(Coordonnees coord, Case[][] plateau) {
        if (coord.estDansPlateau() && symbioseVitalite(coord, plateau) == nbVoisinsMax-1) {
            if (plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_ROUGE) {
                vr -= plateau[coord.ligne][coord.colonne].vitalite;
            } else if (plateau[coord.ligne][coord.colonne].couleur == Utils.CAR_BLEU) {
                vb -= plateau[coord.ligne][coord.colonne].vitalite;
            }
        }
    }

    /**
     * regarder le nombre de plante de la meme couleur que le joueur courant
     * autour de la case à tester
     *
     * @param c coordonnée à tester
     * @param plateau plateau de jeu
     * @param couleurJoueur couleur du joueur
     * @return nb plantes du même joueur autour de la case
     */
    public int symbioseVitaliteMemeCouleur(Coordonnees c, Case[][] plateau, char couleurJoueur) {
        int nbVoisinsMemeCouleur = 0;
        for (Coordonnees coord : lesQuatresVoisins(c)) {
            if (coord.estDansPlateau() && estEspecePlante(coord, plateau)
                    && estCaseVoisineMemeJoueur(coord, plateau, couleurJoueur)) {
                nbVoisinsMemeCouleur++;
            }
        }
        return nbVoisinsMemeCouleur;
    }

    /**
     * retourner les 4 voisins de la case si il en existe
     *
     * @param c la coordonnée d'origine
     * @return les 4 coordonnées voisine
     */
    static public Coordonnees[] lesQuatresVoisins(Coordonnees c) {
        Coordonnees voisinNord = new Coordonnees(c.ligne - 1, c.colonne);
        Coordonnees voisinSud = new Coordonnees(c.ligne + 1, c.colonne);
        Coordonnees voisinOuest = new Coordonnees(c.ligne, c.colonne - 1);
        Coordonnees voisinEst = new Coordonnees(c.ligne, c.colonne + 1);
        Coordonnees[] direction = {voisinNord, voisinSud, voisinEst, voisinOuest};
        return direction;
    }

    /**
     * retourner le nombre de plantes autour de la case d'origine
     *
     * @param c la coordonnée de la case d'origine
     * @param plateau plateau de jeu
     * @return nb voisins qui sont des plantes
     */
    static public int symbioseVitalite(Coordonnees c, Case[][] plateau) {
        int nbVoisins = 0;
        for (Coordonnees coord : lesQuatresVoisins(c)) {
            if (coord.estDansPlateau() && !estCaseVide(plateau, coord)) {
                nbVoisins++;
            }
        }
        return nbVoisins;
    }

    /**
     * ajouter une action de dissemination
     *
     * @param coord la coordonnée de la case à disseminer
     * @param actions tableau d'actions à retourner
     * @param plateau plateau de jeu
     * @param v les vitalités
     * @param couleurJoueur couleur du joueur courant
     */
    public void ajoutActionDisseminer(Coordonnees coord, String[] actions, Case[][] plateau, Vitalite v, char couleurJoueur) {
        vr = v.getVitaliteR();
        vb = v.getVitaliteB();
        String typeReproduction = "";
        for (Plante p : plantesPossibles) {
            if (plateau[coord.ligne][coord.colonne].espece == p.getSymbole()) {
                typeReproduction = p.getReproduction();
            }
        }
        switch (typeReproduction) {
            case "Autofertile":
                disseminationFertile(coord, plateau, couleurJoueur);
                break;
            case "Autostérile":
                disseminationSterile(coord, plateau, couleurJoueur);
                if (couleurJoueur == Utils.CAR_ROUGE) {
                    if (vr == v.getVitaliteR() && nbVoisinsMemeEspece(coord, plateau) == 0) {
                        return;
                    }
                } else {
                    if (vb == v.getVitaliteB() && nbVoisinsMemeEspece(coord, plateau) == 0) {
                        return;
                    }
                }
                break;
            default:

                break;
        }
        String action = "I" + coord.carLigne() + coord.carColonne() + "," + vr + "," + vb;
        actions[nbActions] = action;
        nbActions++;
    }

    /**
     * compter le nombre de voisins de la meme espèce
     *
     * @param coord case d'origine
     * @param plateau plateau de jeu
     * @return le nombre de voisin de la meme espece
     */
    public int nbVoisinsMemeEspece(Coordonnees coord, Case[][] plateau) {
        int nbVoisinsMemeEspece = 0;
        for (Coordonnees c : lesQuatresVoisins(coord)) {
            if (c.estDansPlateau() && estCaseVoisineMemeEspece(coord, c, plateau) && plateau[c.ligne][c.colonne].espece != Utils.CAR_VIDE) {
                nbVoisinsMemeEspece++;
            }
        }
        return nbVoisinsMemeEspece;
    }

    /**
     * disseminer un plante de maniere sterile
     *
     * @param coord coordonnée à tester
     * @param plateau plateau de jeu
     * @param couleurJoueur couleur du joueur courant
     */
    public void disseminationSterile(Coordonnees coord, Case[][] plateau, char couleurJoueur) {
        int nbVoisinsMemeEspece = 0;
        int[] lesVitalitesVoisines = new int[nbVoisinsMax];
        for (Coordonnees c : lesQuatresVoisins(coord)) {
            if (c.estDansPlateau() && estCaseVoisineMemeEspece(coord, c, plateau) && plateau[c.ligne][c.colonne].espece != Utils.CAR_VIDE) {
                lesVitalitesVoisines[nbVoisinsMemeEspece] = plateau[c.ligne][c.colonne].vitalite;
                nbVoisinsMemeEspece++;
            }
        }
        if (nbVoisinsMemeEspece != 0 && nbVoisinsPossible(coord, plateau) != 0) {
            switch (couleurJoueur) {
                case Utils.CAR_ROUGE:
                    vr += (nbVoisinsPossible(coord, plateau) * vitaliteMin(lesVitalitesVoisines, coord, plateau));
                    break;
                case Utils.CAR_BLEU:
                    vb += (nbVoisinsPossible(coord, plateau) * vitaliteMin(lesVitalitesVoisines, coord, plateau));
                    break;
            }
        }
    }

    /**
     * compter le nombre de case vide au tour de l'origine
     *
     * @param c coordonnée de la case d'origine
     * @param plateau plateau de jeu
     * @return nb voisins possible
     */
    static public int nbVoisinsPossible(Coordonnees c, Case[][] plateau) {
        int nbVoisins = 0;
        for (Coordonnees coord : lesQuatresVoisins(c)) {
            if (coord.estDansPlateau() && estCaseVide(plateau, coord)) {
                nbVoisins++;
            }
        }
        return nbVoisins;
    }

    /**
     * selectioner la vitalite min lors de la dissemination sterile
     *
     * @param vitaliteVoisine tableau d'entier comportant les vitalités des
     * cases voisines de la meme especes
     * @param c coordonnée d'origine
     * @param plateau plateau de jeu
     * @return le min des vitalité voisines et de la case d'origine
     */
    static public int vitaliteMin(int[] vitaliteVoisine, Coordonnees c, Case[][] plateau) {
        int miniVitalitéVoisin = vitaliteMax;
        for (int i = 0; i < vitaliteVoisine.length; i++) {
            if (vitaliteVoisine[i] < miniVitalitéVoisin && vitaliteVoisine[i] != 0) {
                miniVitalitéVoisin = vitaliteVoisine[i];
            }
        }
        if (plateau[c.ligne][c.colonne].vitalite < miniVitalitéVoisin) {
            miniVitalitéVoisin = plateau[c.ligne][c.colonne].vitalite;
        }
        return miniVitalitéVoisin;
    }

    /**
     * disseminer une plante de maniere fertile
     *
     * @param coord coordonnée de la plante a dissemminer
     * @param plateau plateau de jeu
     * @param couleurJoueur couleur du joueur courant
     */
    public void disseminationFertile(Coordonnees coord, Case[][] plateau, char couleurJoueur) {
        for (Coordonnees c : lesQuatresVoisins(coord)) {
            if (c.estDansPlateau() && estCaseVide(plateau, c)) {
                switch (couleurJoueur) {
                    case Utils.CAR_ROUGE:
                        vr++;
                        break;
                    case Utils.CAR_BLEU:
                        vb++;
                        break;
                }
            }
        }
    }

    /**
     * checker si 2 cases on la meme espece
     *
     * @param coord case 1
     * @param c case 2
     * @param plateau plateau de jeu
     * @return vrai ou faux
     */
    static public boolean estCaseVoisineMemeEspece(Coordonnees coord, Coordonnees c, Case[][] plateau) {
        return plateau[coord.ligne][coord.colonne].espece == plateau[c.ligne][c.colonne].espece;
    }

    /**
     * voir si case vide
     *
     * @param plateau plateau de jeu
     * @param coord coordonnée
     * @return vrai ou faux
     */
    static public boolean estCaseVide(Case[][] plateau, Coordonnees coord) {
        return plateau[coord.ligne][coord.colonne].espece == Utils.CAR_VIDE;
    }

    /**
     * voir si la case est de meme couleur que le joueur courant
     *
     * @param c coordonnée de la case a tester
     * @param plateau plateau de jeu
     * @param couleurJoueur couleur du joueur courant
     * @return vrai ou faux
     */
    public boolean estCaseVoisineMemeJoueur(Coordonnees c, Case[][] plateau, char couleurJoueur) {
        return plateau[c.ligne][c.colonne].couleur == couleurJoueur;
    }

    /**
     * voir si uneplante est présente
     *
     * @param c la coordonnée de la case à tester
     * @param plateau plateau de jeu
     * @return vrai ou faux
     */
    public boolean estEspecePlante(Coordonnees c, Case[][] plateau) {
        return plateau[c.ligne][c.colonne].espece == Utils.CAR_POMMIER
                || plateau[c.ligne][c.colonne].espece == Utils.CAR_FRAMBOISE
                || plateau[c.ligne][c.colonne].espece == Utils.CAR_HARICOT
                || plateau[c.ligne][c.colonne].espece == Utils.CAR_POMME_DE_TERRE
                || plateau[c.ligne][c.colonne].espece == Utils.CAR_SUREAU
                || plateau[c.ligne][c.colonne].espece == Utils.CAR_TOMATE;
    }
}
