#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Script pour recadrer et renommer les captures d'écran du Play Store."""

from PIL import Image
import os
import sys

# Force UTF-8 encoding for Windows console
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding='utf-8')

# Mapping des fichiers vers leur langue et numéro
# Format: (fichier_source, langue, numéro)
screenshots = [
    # Français (fr) - "Mes Parties", "Yam's", "Mes Jeux", "Mes Joueurs"
    ("WhatsApp Image 2025-11-14 at 11.56.09.jpeg", "fr", 1),
    ("WhatsApp Image 2025-11-14 at 11.56.10.jpeg", "fr", 2),
    ("WhatsApp Image 2025-11-14 at 11.56.11.jpeg", "fr", 3),
    ("WhatsApp Image 2025-11-14 at 11.56.12.jpeg", "fr", 4),

    # Anglais (en) - "My Matches", "Yahtzee", "My Games", "My Players"
    ("WhatsApp Image 2025-11-14 at 11.56.13.jpeg", "en", 1),
    ("WhatsApp Image 2025-11-14 at 11.56.15.jpeg", "en", 2),
    ("WhatsApp Image 2025-11-14 at 11.56.14.jpeg", "en", 3),
    ("WhatsApp Image 2025-11-14 at 11.56.16.jpeg", "en", 4),

    # Espagnol (es) - "Mis Partidas", "Yahtzee", "Mis Juegos", "Mis Jugadores"
    ("WhatsApp Image 2025-11-14 at 11.56.17.jpeg", "es", 1),
    ("WhatsApp Image 2025-11-14 at 11.56.18.jpeg", "es", 2),
    ("WhatsApp Image 2025-11-14 at 11.56.19.jpeg", "es", 3),
    ("WhatsApp Image 2025-11-14 at 11.56.20.jpeg", "es", 4),

    # Allemand (de) - "Meine Partien", "Kniffel", "Meine Spiele", "Meine Spieler"
    ("WhatsApp Image 2025-11-14 at 11.56.23.jpeg", "de", 1),
    ("WhatsApp Image 2025-11-14 at 11.56.24.jpeg", "de", 2),
    ("WhatsApp Image 2025-11-14 at 11.56.22.jpeg", "de", 3),
    ("WhatsApp Image 2025-11-14 at 11.56.20.jpeg", "de", 4),

    # Italien (it) - "Le mie partite", "Yahtzee", "I miei giochi", "I miei giocatori"
    ("WhatsApp Image 2025-11-14 at 11.56.25.jpeg", "it", 1),
    ("WhatsApp Image 2025-11-14 at 11.56.27.jpeg", "it", 2),
    ("WhatsApp Image 2025-11-14 at 11.56.28.jpeg", "it", 3),
    ("WhatsApp Image 2025-11-14 at 11.56.26.jpeg", "it", 4),
]

def process_image(source_file, lang, num):
    """Recadre une image et la sauvegarde avec le nouveau nom."""
    print(f"Traitement: {source_file} -> screenshot_{lang}_{num}.jpeg")

    # Ouvrir l'image
    img = Image.open(source_file)
    width, height = img.size
    print(f"  Dimensions originales: {width}x{height}")

    # Recadrer: enlever 110px en haut (barre orange complète) et 145px en bas (navigation Android)
    crop_top = 110
    crop_bottom = 145

    cropped = img.crop((0, crop_top, width, height - crop_bottom))
    new_width, new_height = cropped.size
    print(f"  Nouvelles dimensions: {new_width}x{new_height}")

    # Sauvegarder avec le nouveau nom
    output_file = f"screenshot_{lang}_{num}.jpeg"
    cropped.save(output_file, "JPEG", quality=95)
    print(f"  OK Sauvegarde: {output_file}\n")

def main():
    """Traite toutes les images."""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(script_dir)

    print(f"Répertoire de travail: {os.getcwd()}\n")
    print(f"Traitement de {len(screenshots)} captures d'écran...\n")

    for source_file, lang, num in screenshots:
        if os.path.exists(source_file):
            process_image(source_file, lang, num)
        else:
            print(f"ATTENTION Fichier non trouve: {source_file}\n")

    print("OK Traitement termine!")

if __name__ == "__main__":
    main()
