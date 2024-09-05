import {
    ClassicEditor,
    AccessibilityHelp,
    Alignment,
    AutoLink,
    Autosave,
    BlockQuote,
    Bold,
    Essentials,
    Heading,
    HorizontalLine,
    Indent,
    IndentBlock,
    Italic,
    Link,
    Paragraph,
    Strikethrough,
    Underline,
    Undo
} from 'ckeditor5';
import translations from 'ckeditor5/translations/ko.js';

const ckEditorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'horizontalLine',
            'link',
            'blockQuote',
            '|',
            'alignment',
            '|',
            'indent',
            'outdent',
            '|',
            'accessibilityHelp'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AccessibilityHelp,
        Alignment,
        AutoLink,
        Autosave,
        BlockQuote,
        Bold,
        Essentials,
        Heading,
        HorizontalLine,
        Indent,
        IndentBlock,
        Italic,
        Link,
        Paragraph,
        Strikethrough,
        Underline,
        Undo
    ],
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    initialData: '',
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },
    placeholder: '',
    language: 'ko',
    translations: [translations]
};


export class CKEditor {
    static instance = null;
    static promise = null;

    constructor(idProperty) {
        if (CKEditor.instance) {
            CKEditor.instance.destroy();
        }
        CKEditor.promise = ClassicEditor.create(document.getElementById(idProperty), ckEditorConfig)
            .then(editor => {
                CKEditor.instance = editor;
            });

        this.initialContent = null;
    }

    async initialize(content) {
        await CKEditor.promise;

        this.initialContent = content;
        this.setContent(content);
    }

    async reset() {
        await CKEditor.promise;

        await this.clear();
        if (this.initialContent) {
            await this.setContent(this.initialContent);
        }
    }

    async clear() {
        await CKEditor.promise;
        this.setContent('');
    }

    async setContent(content) {
        await CKEditor.promise;
        CKEditor.instance.setData(content);
    }

    async getContent() {
        await CKEditor.promise;
        return CKEditor.instance.getData();
    }
}


class MarkerTool {
    constructor({api}) {
        this.api = api;
        this.button = null;
        this.tag = 'MARK';

        this.iconClasses = {
            base: this.api.styles.inlineToolButton,
            active: this.api.styles.inlineToolButtonActive
        };
    }

    static get isInline() {
        return true;
    }

    render() {
        this.button = document.createElement('button');
        this.button.type = 'button';
        this.button.classList.add(this.iconClasses.base);
        this.button.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
             fill="none" viewBox="0 0 24 24">
            <path stroke="currentColor" stroke-width="2"
                  d="M11.3536 9.31802L12.7678 7.90381C13.5488 7.12276 14.8151 7.12276 15.5962 7.90381C16.3772 8.68486 16.3772 9.95119 15.5962 10.7322L14.182 12.1464M11.3536 9.31802L7.96729 12.7043C7.40889 13.2627 7.02827 13.9739 6.8734 14.7482L6.69798 15.6253C6.55804 16.325 7.17496 16.942 7.87468 16.802L8.75176 16.6266C9.52612 16.4717 10.2373 16.0911 10.7957 15.5327L14.182 12.1464M11.3536 9.31802L14.182 12.1464"></path>
            <line x1="15" x2="19" y1="17" y2="17" stroke="currentColor"
                  stroke-linecap="round" stroke-width="2"></line>
        </svg>`;

        return this.button;
    }

    surround(range) {
        if (!range) {
            return;
        }

        let termWrapper = this.api.selection.findParentTag(this.tag, Marker.CSS);

        if (termWrapper) {
            this.unwrap(termWrapper);
        } else {
            this.wrap(range);
        }
    }

    wrap(range) {
        let marker = document.createElement(this.tag);

        marker.classList.add(Marker.CSS);

        marker.appendChild(range.extractContents());
        range.insertNode(marker);

        const markerColor = document.getElementById('markerColor');
        if (markerColor) {
            marker.style.cssText = `background: ${markerColor.value};`;
        }

        this.api.selection.expandToTag(marker);
    }

    unwrap(termWrapper) {
        this.api.selection.expandToTag(termWrapper);

        let sel = window.getSelection();
        let range = sel.getRangeAt(0);

        let unwrappedContent = range.extractContents();

        termWrapper.parentNode.removeChild(termWrapper);

        range.insertNode(unwrappedContent);

        sel.removeAllRanges();
        // sel.addRange(range);
    }

    checkState() {
        const termTag = this.api.selection.findParentTag(this.tag, Marker.CSS);

        this.button.classList.toggle(this.iconClasses.active, !!termTag);
    }

    static get sanitize() {
        return {
            mark: {
                class: Marker.CSS
            }
        };
    }
}

const editorTools = {
    marker: {
        class: MarkerTool,
        shortcut: 'CMD+SHIFT+M',
    }
};

export class SimpleEditor {
    static instance = null;

    constructor(idProperty, tools = []) { // tools = [ ... ]
        if (SimpleEditor.instance) {
            SimpleEditor.instance.destroy();
        }

        const toolsConfig = {};
        tools.forEach(tool => {
            if (editorTools[tool]) {
                toolsConfig[tool] = editorTools[tool];
            }
        });

        SimpleEditor.instance = new EditorJS({
            holder: idProperty,
            inlineToolbar: ['bold', 'italic', 'link', ...tools],
            tools: toolsConfig
        });

        this.initialContent = null;
    }

    async initialize(content) {
        await SimpleEditor.instance.isReady;

        this.initialContent = content;
        this.setContent(content);
    }

    async reset() {
        await SimpleEditor.instance.isReady;

        await this.clear();
        if (this.initialContent) {
            this.setContent(this.initialContent);
        }
    }

    async clear() {
        await SimpleEditor.instance.isReady;
        SimpleEditor.instance.blocks.clear();
    }

    async setContent(content) {
        await SimpleEditor.instance.isReady;

        const dataBlocks = this.convertHtmlToData(content);
        SimpleEditor.instance.blocks.render({blocks: dataBlocks});
    }

    async getContent() {
        await SimpleEditor.instance.isReady;

        const outputData = await SimpleEditor.instance.save();
        return this.convertDataToHtml(outputData.blocks);
    }

    convertHtmlToData(htmlString) {
        const elements = Array.from(
            new DOMParser().parseFromString(htmlString, 'text/html').querySelectorAll('p')
        );
        return elements.map(p => ({
            type: "paragraph",
            data: { text: p.innerHTML }
        }));
    }

    convertDataToHtml(blocks) {
        let convertedHtml = '';
        blocks.map(block => {
            const text = block.data.text;
            const isBlank = text.replaceAll('&nbsp;', '').trim() === '';
            if (!isBlank) {
                switch (block.type) {
                    case 'paragraph':
                        convertedHtml += `<p>${text}</p>`;
                        break;
                    default:
                        console.log('Unknown block type', block.type);
                        break;
                }
            }
        });
        return convertedHtml;
    }
}
