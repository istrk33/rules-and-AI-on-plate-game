package biosphere7;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Votre IA pour le jeu Biosphere7, au niveau 5.
 */
public class IABiosphere7 {

    /**
     * Hôte du grand ordonnateur.
     */
    String hote = null;

    /*TOUR DE JEU*/
    int tourCourant;

    String aDA = "";
    /**
     * Port du grand ordonnateur.
     */
    int port = -1;

    /**
     * Couleur de votre joueur (IA) : 'R'ouge ou 'B'leu.
     */
    final char couleur;

    /**
     * Interface pour le protocole du grand ordonnateur.
     */
    TcpGrandOrdonnateur grandOrdo = null;

    /**
     * Taille du plateau.
     */
    static final int TAILLE = 14;

    /**
     * Nombre maximal de tours de jeu.
     */
    static final int NB_TOURS_JEU_MAX = 40;

    /**
     * Constructeur.
     *
     * @param hote Hôte.
     * @param port Port.
     * @param uneCouleur Couleur de ce joueur
     */
    public IABiosphere7(String hote, int port, char uneCouleur) {
        this.hote = hote;
        this.port = port;
        this.grandOrdo = new TcpGrandOrdonnateur();
        this.couleur = uneCouleur;
    }

    /**
     * Connexion au Grand Ordonnateur.
     *
     * @throws IOException exception sur les entrées/sorties
     */
    void connexion() throws IOException {
        System.out.print(
                "Connexion au Grand Ordonnateur : " + hote + " " + port + "...");
        System.out.flush();
        grandOrdo.connexion(hote, port);
        System.out.println(" ok.");
        System.out.flush();
    }

    /**
     * Boucle de jeu : envoi des actions que vous souhaitez jouer, et réception
     * des actions de l'adversaire.
     *
     * @throws IOException exception sur les entrées/sorties
     */
    void toursDeJeu() throws IOException {
        // paramètres
        System.out.println("Je suis le joueur " + couleur + ".");
        // le plateau initial
        System.out.println("Réception du plateau initial...");
        Case[][] plateau = grandOrdo.recevoirPlateauInitial();
        System.out.println("Plateau reçu.");
        // compteur de tours de jeu (entre 1 et 40)
        int nbToursJeu = 1;
        // la couleur du joueur courant (change à chaque tour de jeu)
        char couleurTourDeJeu = Utils.CAR_ROUGE;
        // booléen pour détecter la fin du jeu
        boolean fin = false;

        while (!fin) {

            tourCourant = nbToursJeu;
            boolean disqualification = false;

            if (couleurTourDeJeu == couleur) {
                // à nous de jouer !
                jouer(plateau, nbToursJeu);
            } else {
                // à l'adversaire de jouer : on récupère son action
                System.out.println("Attente de réception action adversaire...");
                String actionAdversaire = grandOrdo.recevoirAction();
                System.out.println("Action adversaire reçue : " + actionAdversaire);
                if ("Z".equals(actionAdversaire)) {
                    System.out.println("L'adversaire est disqualifié.");
                    disqualification = true;
                } else if (!fin) {
                    System.out.println("L'adversaire joue : "
                            + actionAdversaire + ".");
                    mettreAJour(plateau, actionAdversaire, couleurTourDeJeu);
                    aDA = actionAdversaire;
                }
            }
            if (nbToursJeu == NB_TOURS_JEU_MAX || disqualification) {
                // fini
                fin = true;
            } else {
                // au suivant
                nbToursJeu++;
                couleurTourDeJeu = suivant(couleurTourDeJeu);
            }
        }
    }

    String meilleureAction(ArrayList<String> liste, char couleur) {
        String a = "";
        if (couleur == Utils.CAR_ROUGE) {
            int dd = -100;
            for (String s : liste) {
                int r = Integer.valueOf(s.substring(s.indexOf(',') + 1, s.lastIndexOf(',')));
                int b = Integer.valueOf(s.substring(s.lastIndexOf(',') + 1, s.length()));
                int d = r - b;
                if (dd < d) {
                    dd = d;
                    a = s;
                }
            }
        } else if (couleur == Utils.CAR_BLEU) {
            int dd = -100;
            for (String s : liste) {
                int r = Integer.valueOf(s.substring(s.indexOf(',') + 1, s.lastIndexOf(',')));
                int b = Integer.valueOf(s.substring(s.lastIndexOf(',') + 1, s.length()));
                int d = b - r;
                if (dd < d) {
                    dd = d;
                    a = s;
                }
            }
        }
        return a;
    }

    /**
     * Fonction exécutée lorsque c'est à notre tour de jouer. Cette fonction
     * envoie donc l'action choisie au serveur.
     *
     * @param plateau le plateau de jeu
     * @param nbToursJeu numéro du tour de jeu
     * @throws IOException exception sur les entrées / sorties
     */
    void jouer(Case[][] plateau, int nbToursJeu) throws IOException {
        JoueurBiosphere7 joueurBiosphere7 = new JoueurBiosphere7();
        String[] actionsPossibles = Utils.nettoyerTableau(
                joueurBiosphere7.actionsPossibles(plateau, couleur, 8));
        if (actionsPossibles.length > 0) {
            String aJ = "";
            String aA1 = "";
            String aF1 = "";
            String aA2 = "";
            String aF2 = "";
            //liste des coups du tour x+2, donc de notre prochain tour
            ArrayList<String> lTF = new ArrayList<>();
            //on crée un platreau qui sert de table d'expérience pour calucler les coups futurs
            Case[][] pT = new Case[plateau.length][plateau[0].length];
            //on crée une liste d'actions de l'adversaire, donc au tour x+1 et de nous au tour x+2
            ArrayList<String> laA1;
            ArrayList<String> laF1;
            ArrayList<String> laA2;
            ArrayList<String> laF2;
            //on switch de couleur car on passe de tour
            char cA = suivant(couleur);
            if (tourCourant == 2 && (aDA.charAt(0) == 'T' || aDA.charAt(0) == 'H')) {
                Coordonnees coord = new Coordonnees(1, 1);
                if (plateau[coord.ligne][coord.colonne].espece == Utils.CAR_VIDE && JoueurBiosphere7.nbVoisinsPossible(coord, plateau) == 4) {
                    aJ = "HbB";
                } else {
                    aJ = "HmM";
                }

            } else {
                for (String s : actionsPossibles) {
                    //pour chaque actions (on peut réduire la taille des actions choisi au début) on va regarder le coup que cela nous offre après avoir calculé le coup de l'adversaire

                    //on commence avec un plateau correct
                    for (int i = 0; i < plateau.length; i++) {
                        for (int j = 0; j < plateau[0].length; j++) {
                            Coordonnees c = new Coordonnees(i, j);
                            Case caseBg = new Case(plateau[c.ligne][c.colonne].espece, plateau[c.ligne][c.colonne].couleur, plateau[c.ligne][c.colonne].vitalite, plateau[c.ligne][c.colonne].nature);
                            pT[c.ligne][c.colonne] = caseBg;
                        }
                    }
                    //on met le plateau a jour simulant qu'on décide de jouer l'action : s
                    mettreAJour(pT, s, couleur);

                    //on calcule alors l'action que l'adversaire choisirait
                    laA1 = new ArrayList<String>(Arrays.asList(Utils.nettoyerTableau(joueurBiosphere7.actionsPossibles(pT, cA, 8))));

                    //on sélectionne l'action de l'adversaire (celle qui a le plus grand écart de vitalité pour lui
                    aA1 = meilleureAction(laA1, cA);

                    //on met à jour le plateau selon la meilleure action
                    mettreAJour(pT, aA1, cA);

                    //on calcule pour ce plateau quelles sont les actions possibles
                    laF1 = new ArrayList<String>(Arrays.asList(Utils.nettoyerTableau(joueurBiosphere7.actionsPossibles(pT, couleur, 8))));

                    //on regarde la meilleure action de cette liste
                    aF1 = meilleureAction(laF1, couleur);

                    //un tour supplémentaire
                    mettreAJour(pT, aF1, couleur);
                    laA2 = new ArrayList<String>(Arrays.asList(Utils.nettoyerTableau(joueurBiosphere7.actionsPossibles(pT, cA, 8))));
                    aA2 = meilleureAction(laA2, cA);
                    mettreAJour(pT, aA2, cA);
                    laF2 = new ArrayList<String>(Arrays.asList(Utils.nettoyerTableau(joueurBiosphere7.actionsPossibles(pT, couleur, 8))));
                    aF2 = meilleureAction(laF2, couleur);

                    //et on la stocke dans l'array liste pour pouvoir les comparer.
                    lTF.add(aF2);
                }

                //on prend la meilleure actions de l'arrayList
                //et c'est l'action que l'on joue !
                aJ = enleverPointsVitalite(meilleureAction(lTF, couleur));
            }

            // jouer l'action
            System.out.println("On joue : " + aJ);
            grandOrdo.envoyerAction(aJ);
            mettreAJour(plateau, aJ, couleur);
        } else {
            // Problème : le serveur vous demande une action alors que vous n'en
            // trouvez plus...
            System.out.println("Aucun action trouvée : abandon...");
            grandOrdo.envoyerAction("ABANDON");
        }

    }

    /**
     * Retourne l'action sans les points de vitalité. Par exemple, sur
     * "PfL,4,5", cela renvoit "PfL".
     *
     * @param actionPdv l'action avec des points de vie
     * @return l'action sans les points de vie
     */
    static String enleverPointsVitalite(String actionPdv) {
        int posVirgule = actionPdv.indexOf(",");
        return posVirgule == -1 ? actionPdv : actionPdv.substring(0, posVirgule);
    }

    /**
     * Calcule la couleur du prochain joueur.
     *
     * @param couleurCourante la couleur du joueur courant
     * @return la couleur du prochain joueur
     */
    static char suivant(char couleurCourante) {
        return couleurCourante == Utils.CAR_ROUGE
                ? Utils.CAR_BLEU : Utils.CAR_ROUGE;
    }

    /**
     * Mettre à jour le plateau suite à une action, supposée valide pour le
     * niveau 5. Vous ne devez pas modifier cette méthode.
     *
     * @param plateau le plateau
     * @param action l'action à appliquer
     * @param couleurCourante couleur du joueur courant
     */
    void mettreAJour(Case[][] plateau, String action, char couleurCourante) {
        // vérification des arguments
        if (plateau == null || action == null || action.length() != 3) {
            return;
        }
        // coordonnées et case
        Coordonnees coord = Coordonnees.depuisCars(action.charAt(1), action.charAt(2));
        switch (action.charAt(0)) {
            case 'P':
                planter(coord, plateau, couleurCourante, Utils.CAR_POMMIER);
                break;
            case 'S':
                planter(coord, plateau, couleurCourante, Utils.CAR_SUREAU);
                break;
            case 'B':
                planter(coord, plateau, couleurCourante, Utils.CAR_FRAMBOISE);
                break;
            case 'D':
                planter(coord, plateau, couleurCourante, Utils.CAR_POMME_DE_TERRE);
                break;
            case 'H':
                planter(coord, plateau, couleurCourante, Utils.CAR_HARICOT);
                break;
            case 'T':
                planter(coord, plateau, couleurCourante, Utils.CAR_TOMATE);
                break;
            case 'C':
                couper(coord, plateau);
                break;
            case 'F':
                fertiliser(coord, plateau);
                break;
            case 'I':
                disseminer(coord, plateau, couleurCourante);
                break;
            default:
                System.out.println("Type d'action incorrect : " + action.charAt(0));
        }
    }

    static void fertiliser(Coordonnees coord, Case[][] plateau) {
        Case laCase = plateau[coord.ligne][coord.colonne];
        /*for (Plante p : JoueurBiosphere7.plantesPossibles) {
            if (plateau[coord.ligne][coord.colonne].espece == p.getSymbole()) {
                laCase.vitalite = augmenterVitalite(laCase, p.getMonType().getBonusFertilisation());
            }
        }*/
        boolean trouve = false;
        int i = 0;
        while (!trouve && i < JoueurBiosphere7.plantesPossibles.length) {
            if (plateau[coord.ligne][coord.colonne].espece == JoueurBiosphere7.plantesPossibles[i].getSymbole()) {
                laCase.vitalite = augmenterVitalite(laCase, JoueurBiosphere7.plantesPossibles[i].getMonType().getBonusFertilisation());
                trouve = true;
            }
            i++;
        }
    }

    static int augmenterVitalite(Case c, int nbAjoutV) {
        if (c.vitalite + nbAjoutV < JoueurBiosphere7.vitaliteMax) {
            c.vitalite += nbAjoutV;
        } else {
            c.vitalite = JoueurBiosphere7.vitaliteMax;
        }
        return c.vitalite;
    }

    static void disseminer(Coordonnees coord, Case[][] plateau, char couleurCourante) {
        Case laCase = plateau[coord.ligne][coord.colonne];
        for (Plante p : JoueurBiosphere7.plantesPossibles) {
            if (p.getReproduction().equals("Autofertile")) {
                for (Coordonnees c : JoueurBiosphere7.lesQuatresVoisins(coord)) {
                    if (c.estDansPlateau() && JoueurBiosphere7.estCaseVide(plateau, c)) {
                        Case nouvellePlante = plateau[c.ligne][c.colonne];
                        ajouterPlante(nouvellePlante, couleurCourante, laCase.espece, plateau, 1);
                    }
                }
            } else if (p.getReproduction().equals("Autostérile")) {
                int nbVoisinsMemeEspece = 0;
                int[] lesVitalitesVoisines = new int[JoueurBiosphere7.nbVoisinsMax];
                for (Coordonnees c : JoueurBiosphere7.lesQuatresVoisins(coord)) {
                    if (c.estDansPlateau() && JoueurBiosphere7.estCaseVoisineMemeEspece(coord, c, plateau) && plateau[c.ligne][c.colonne].espece != Utils.CAR_VIDE) {
                        lesVitalitesVoisines[nbVoisinsMemeEspece] = plateau[c.ligne][c.colonne].vitalite;
                        nbVoisinsMemeEspece++;
                    }
                }
                if (nbVoisinsMemeEspece != 0 && JoueurBiosphere7.nbVoisinsPossible(coord, plateau) != 0) {
                    int v = JoueurBiosphere7.vitaliteMin(lesVitalitesVoisines, coord, plateau);
                    for (Coordonnees c : JoueurBiosphere7.lesQuatresVoisins(coord)) {
                        if (c.estDansPlateau() && JoueurBiosphere7.estCaseVide(plateau, c)) {
                            Case nouvellePlante = plateau[c.ligne][c.colonne];
                            ajouterPlante(nouvellePlante, couleurCourante, laCase.espece, plateau, v);
                        }
                    }
                }
            }
        }
    }

    /**
     * Planter un pommier sur une case donnée.
     *
     * @param coord coordonnées de la case
     * @param plateau le plateau de jeu
     * @param couleurCourante la couleur du joueur courant
     */
    static void planter(Coordonnees coord, Case[][] plateau, char couleurCourante, char charPlante) {
        if (JoueurBiosphere7.symbioseVitalite(coord, plateau) < JoueurBiosphere7.nbVoisinsMax) {
            Case laCase = plateau[coord.ligne][coord.colonne];
            ajouterPlante(laCase, couleurCourante, charPlante, plateau, (1 + nbVoisinesJoueur(coord, plateau, couleurCourante)));
        }
        for (Coordonnees c : JoueurBiosphere7.lesQuatresVoisins(coord)) {
            if (JoueurBiosphere7.symbioseVitalite(c, plateau) == 4) {
                Case aTej = plateau[c.ligne][c.colonne];
                retirerPlante(aTej);
            }
        }
    }

    static void ajouterPlante(Case laCase, char couleurCourante, char charPlante, Case[][] plateau, int vitalite) {
        laCase.couleur = couleurCourante;
        laCase.espece = charPlante;
        laCase.vitalite = vitalite;
    }

    /**
     * Nombre de cases voisines d'une case et contenant une plante du joueur.
     *
     * @param coord la case dont on souhaite analyser les voisines
     * @param plateau le plateau courant
     * @param couleurCourante la couleur du joueur courant
     * @return le nombre de cases voisines contenant une plante du joueur
     */
    static int nbVoisinesJoueur(Coordonnees coord, Case[][] plateau, char couleurCourante) {
        return (int) voisines(coord)
                .map(v -> plateau[v.ligne][v.colonne])
                .filter(c -> c.espece != Utils.CAR_VIDE)
                .filter(c -> c.couleur == couleurCourante)
                .count();
    }

    /**
     * Les coordonnées des cases voisines dans le plateau.
     *
     * @param coord les coordonnées de la case d'origine
     * @return les coordonnées des cases voisines
     */
    static Stream<Coordonnees> voisines(final Coordonnees coord) {
        return Stream.of(new int[][]{{-1, 0}, {0, -1}, {1, 0}, {0, 1}})
                .map(d -> new Coordonnees(coord.ligne + d[0], coord.colonne + d[1]))
                .filter(v -> 0 <= v.ligne && v.ligne < 14)
                .filter(v -> 0 <= v.colonne && v.colonne < 14);
    }

    /**
     * Couper une plante sur une case donnée.
     *
     * @param coord coordonnées de la case
     * @param plateau le plateau de jeu
     */
    static void couper(Coordonnees coord, Case[][] plateau) {
        retirerPlante(plateau[coord.ligne][coord.colonne]);
        voisines(coord)
                .map(v -> plateau[v.ligne][v.colonne])
                .filter(c -> c.espece != Utils.CAR_VIDE)
                .forEach(c -> c.vitalite = Math.min(9, c.vitalite + 1));
    }

    /**
     * Retirer une plante d'une case.
     *
     * @param laCase la case dont on doit retirer la plante
     */
    static void retirerPlante(Case laCase) {
        laCase.espece = Utils.CAR_VIDE;
        laCase.couleur = Utils.CAR_ROUGE;
        laCase.vitalite = 0;
    }

    /**
     * Programme principal. Il sera lancé automatiquement, ce n'est pas à vous
     * de le lancer.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("Démarrage le " + format.format(new Date()));
        System.out.flush();
        // « create » du protocole du grand ordonnateur.
        final String USAGE
                = System.lineSeparator()
                + "\tUsage : java " + IABiosphere7.class.getName()
                + " <hôte> <port> <ordre>";
        if (args.length != 3) {
            System.out.println("Nombre de paramètres incorrect." + USAGE);
            System.out.flush();
            System.exit(1);
        }
        String hote = args[0];
        int port = -1;
        try {
            port = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Le port doit être un entier." + USAGE);
            System.out.flush();
            System.exit(1);
        }
        int ordre = -1;
        try {
            ordre = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("L'ordre doit être un entier." + USAGE);
            System.out.flush();
            System.exit(1);
        }
        try {
            char couleurJoueur = (ordre == 1 ? 'R' : 'B');
            IABiosphere7 iaLowatem = new IABiosphere7(hote, port, couleurJoueur);
            iaLowatem.connexion();
            iaLowatem.toursDeJeu();
        } catch (IOException e) {
            System.out.println("Erreur à l'exécution du programme : \n" + e);
            System.out.flush();
            System.exit(1);
        }
    }
}
