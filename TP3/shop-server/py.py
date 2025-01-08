import os
import re

# Dossier contenant les fichiers source
SOURCE_DIR = "./src/main/java/fr/fullstack/shopapp/"

# Remplacements des imports
IMPORT_REPLACEMENTS = {
    "io.swagger.annotations.ApiOperation": "io.swagger.v3.oas.annotations.Operation",
    "io.swagger.annotations.ApiImplicitParam": "io.swagger.v3.oas.annotations.Parameter",
    "io.swagger.annotations.ApiImplicitParams": "io.swagger.v3.oas.annotations.Parameters",
    "io.swagger.annotations.ApiParam": "io.swagger.v3.oas.annotations.Parameter",
    "javax.validation.Valid": "jakarta.validation.Valid",
    "javax.validation.constraints.NotNull": "jakarta.validation.constraints.NotNull",
    "javax.validation.constraints.Size": "jakarta.validation.constraints.Size",
    "javax.persistence.Column": "jakarta.persistence.Column",
    "javax.persistence.Entity": "jakarta.persistence.Entity",
    "javax.persistence.GeneratedValue": "jakarta.persistence.GeneratedValue",
    "javax.persistence.GenerationType": "jakarta.persistence.GenerationType",
    "javax.persistence.Id": "jakarta.persistence.Id",
    "javax.persistence.ManyToMany": "jakarta.persistence.ManyToMany",
    "javax.persistence.Table": "jakarta.persistence.Table",
    "javax.validation.constraints.Max": "jakarta.validation.constraints.Max",
    "javax.validation.constraints.Min": "jakarta.validation.constraints.Min",
    "javax.persistence.CascadeType":"jakarta.persistence.CascadeType",
    "javax.persistence.JoinColumn":"jakarta.persistence.JoinColumn",
    "javax.persistence.JoinTable":"jakarta.persistence.JoinTable",
    "javax.persistence.ManyToOne":"jakarta.persistence.ManyToOne",
    "javax.persistence.OneToMany":"jakarta.persistence.OneToMany",
    "javax.validation.constraints.PositiveOrZero":"jakarta.validation.constraints.PositiveOrZero",
    "javax.persistence.FetchType":"jakarta.persistence.FetchType",
    "javax.persistence.PersistenceContext":"jakarta.persistence.PersistenceContext",
    "javax.validation.Payload":"jakarta.validation.Payload",
    "javax.validation.Constraint":"jakarta.validation.Constraint"
}


# Remplacements des annotations
ANNOTATION_REPLACEMENTS = [
    # Remplacement de @ApiOperation
    (r'@ApiOperation\(value\s*=\s*"(.*?)"\)', r'@Operation(summary = "\1")'),

    # Remplacement de @ApiImplicitParams
    (r'@ApiImplicitParams\s*\(\{\s*([\s\S]*?)\s*\}\)', r'@Parameters({\1})'),

    # Remplacement de @ApiImplicitParam
    (r'@ApiImplicitParam\(name\s*=\s*"(.*?)",[\s]*dataType\s*=\s*"(.*?)",[\s]*paramType\s*=\s*"(.*?)",[\s]*value\s*=\s*"(.*?)",[\s]*defaultValue\s*=\s*"(.*?)"\)',
     r'@Parameter(name = "\1", description = "\4", example = "\5", in = io.swagger.v3.oas.annotations.enums.ParameterIn.\3)'),

    # Remplacement de @ApiParam
    (r'@ApiParam\(value\s*=\s*"(.*?)"(?:, example\s*=\s*"(.*?)")?\)',
     r'@Parameter(description = "\1", example = "\2")'),
    (r'ParameterIn\.query', r'ParameterIn.QUERY'),

    (r'(@Parameter\([^)]*?),\s*example\s*=\s*"(.*?)",\s*example\s*=\s*"(.*?)"', r'\1, example = "\2"')


]



def replace_in_file(file_path):
    absolute_path = os.path.abspath(file_path)  # Obtenez le chemin absolu
    print(f"Traitement du fichier : {absolute_path}") 

    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()

    # Remplacer les imports
    for old_import, new_import in IMPORT_REPLACEMENTS.items():
        content = content.replace(old_import, new_import)

    # Remplacer les annotations
    for pattern, replacement in ANNOTATION_REPLACEMENTS:
        content = re.sub(pattern, replacement, content, flags=re.DOTALL)

    # Nettoyer les chaînes échappées inutiles
    content = content.replace(r'\"', '"')

    # Écrire les modifications
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(content)


def process_directory(directory):
    found_files = False
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                found_files = True
                file_path = os.path.join(root, file)
                absolute_path = os.path.abspath(file_path)
                print(f"En cours de traitement : {absolute_path}")
                replace_in_file(file_path)
                print(f"Processed: {file_path}")
    if not found_files:
        print(f"Aucun fichier trouvé dans le répertoire : {os.path.abspath(directory)}")


if __name__ == "__main__":
    process_directory(SOURCE_DIR)
